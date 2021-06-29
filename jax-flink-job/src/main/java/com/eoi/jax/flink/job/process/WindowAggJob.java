/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eoi.jax.flink.job.process;

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkProcessJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.api.tuple.Tuple2;
import com.eoi.jax.common.OperatorCategory;
import com.eoi.jax.flink.job.common.KeySelectorForMap;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.OutputTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

import static com.eoi.jax.flink.job.process.WindowAggJobConfig.KEY_SPLITTER;
import static com.eoi.jax.flink.job.process.WindowAggJobConfig.WINDOW_TRIGGER_COUNT;
import static com.eoi.jax.flink.job.process.WindowAggJobConfig.WINDOW_TRIGGER_DELTA;
import static com.eoi.jax.flink.job.process.WindowAggJobConfig.WINDOW_TRIGGER_EXPRESSION;
import static com.eoi.jax.flink.job.process.WindowAggJobConfig.WINDOW_TYPE_GLOBAL;
import static com.eoi.jax.flink.job.process.WindowAggJobConfig.WINDOW_TYPE_SESSION;
import static com.eoi.jax.flink.job.process.WindowAggJobConfig.WINDOW_TYPE_SLIDING;
import static com.eoi.jax.flink.job.process.WindowAggJobConfig.WINDOW_TYPE_TUMBLING;

@Job(
        name = "WindowAggJob",
        display = "窗口聚合",
        description = "窗口聚合统计，数值类型支持 `max`,`min`,`avg`,`sum`；所有类型支持 `last`,`first`,`count`,`collect`,`calc`",
        icon = "WindowAggJob.svg",
        doc = "WindowAggJob.md",
        category = OperatorCategory.WINDOW_OPERATION
)
public class WindowAggJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        Tuple2<DataStream<Map<String, Object>>, DataStream<Map<String, Object>>>,
        WindowAggJobConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(WindowAggJob.class);

    @Override
    public Tuple2<DataStream<Map<String, Object>>, DataStream<Map<String, Object>>> build(
            FlinkEnvironment context,
            DataStream<Map<String,Object>> dataStream,
            WindowAggJobConfig config,
            JobMetaConfig metaConfig) throws Throwable {

        TimeCharacteristic timeCharacteristic = context.streamEnv.getStreamTimeCharacteristic();

        // 根据TimeCharacteristic和window type 来设置WindowAssigner
        WindowAssigner windowAssigner = null;
        if (WINDOW_TYPE_GLOBAL.equals(config.getWindowType())) {
            windowAssigner = GlobalWindows.create();
        } else {
            Time windowSizeTime = Time.milliseconds(config.getWindowSize());
            if (timeCharacteristic.equals(TimeCharacteristic.EventTime)) {
                if (WINDOW_TYPE_TUMBLING.equals(config.getWindowType())) {
                    windowAssigner = TumblingEventTimeWindows.of(Time.milliseconds(config.getWindowSize()));
                }
                if (WINDOW_TYPE_SLIDING.equals(config.getWindowType())) {
                    windowAssigner = SlidingEventTimeWindows.of(windowSizeTime, Time.milliseconds(config.getSlidingSize()));
                }
                if (WINDOW_TYPE_SESSION.equals(config.getWindowType())) {
                    windowAssigner = EventTimeSessionWindows.withGap(windowSizeTime);
                }
            } else {
                if (WINDOW_TYPE_TUMBLING.equals(config.getWindowType())) {
                    windowAssigner = TumblingProcessingTimeWindows.of(windowSizeTime);
                }
                if (WINDOW_TYPE_SLIDING.equals(config.getWindowType())) {
                    windowAssigner = SlidingProcessingTimeWindows.of(windowSizeTime, Time.milliseconds(config.getSlidingSize()));
                }
                if (WINDOW_TYPE_SESSION.equals(config.getWindowType())) {
                    windowAssigner = ProcessingTimeSessionWindows.withGap(windowSizeTime);
                }
            }
        }

        // 设置分组字段
        KeySelectorForMap keySelector = new KeySelectorForMap(config.getGroupKeys(), "",KEY_SPLITTER,"");
        OutputTag<Map<String, Object>> laterTag = new OutputTag<Map<String, Object>>(metaConfig.getJobId() + "laterTag"){};

        WindowedStream<Map<String, Object>, String, TimeWindow> windowedStream = dataStream.keyBy(keySelector).window(windowAssigner);

        // 设置自定义窗口触发器
        if (WINDOW_TRIGGER_COUNT.equals(config.getTriggerType())) {
            windowedStream = windowedStream.trigger(WindowTriggerByCount.of(config.getTriggerMaxCount(), TriggerResult.valueOf(config.getTriggerResultType())));
        }

        if (WINDOW_TRIGGER_DELTA.equals(config.getTriggerType())) {
            WindowTriggerDeltaFunction deltaFunction = new WindowTriggerDeltaFunction(config.getTriggerDeltaValueField(), config.getTriggerDeltaExp());
            TypeInformation<Map<String, Object>> typeInfo = TypeInformation.of(new TypeHint<Map<String, Object>>(){});
            ExecutionConfig executionConfig = dataStream.getExecutionConfig();
            windowedStream = windowedStream.trigger(WindowTriggerByDelta.of(
                    config.getTriggerDeltaThreshold(),
                    deltaFunction,
                    typeInfo.createSerializer(executionConfig),
                    TriggerResult.valueOf(config.getTriggerResultType())));
        }

        if (WINDOW_TRIGGER_EXPRESSION.equals(config.getTriggerType())) {
            windowedStream = windowedStream.trigger(WindowTriggerByExp.of(config.getTriggerExpression(), TriggerResult.valueOf(config.getTriggerResultType())));
        }

        // 聚合输出
        SingleOutputStreamOperator<Map<String, Object>> output = windowedStream
                .allowedLateness(Time.seconds(config.getAllowedLatenessTime()))
                .sideOutputLateData(laterTag)
                .aggregate(new WindowAggFunction(config), new WindowProcessFunction(config))
                .name(metaConfig.getJobEntry())
                .uid(metaConfig.getJobId());

        DataStream<Map<String, Object>> laterStream = output.getSideOutput(laterTag);

        return Tuple2.of(output, laterStream);
    }

    @Override
    public WindowAggJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        WindowAggJobConfig config = new WindowAggJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        if (config.getGroupKeys() == null) {
            config.setGroupKeys(new ArrayList<>());
        }
        return config;
    }
}
