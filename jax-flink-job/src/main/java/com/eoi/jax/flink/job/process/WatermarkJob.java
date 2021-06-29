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
import com.eoi.jax.common.OperatorCategory;
import com.eoi.jax.flink.job.common.TimestampConvertJobConfig;
import com.eoi.jax.flink.job.common.TimestampConvertUtil;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.eoi.jax.flink.job.process.WatermarkJobConfig.WATERMARK_SOURCE_TIME_FIELD;


@Job(
        name = "WatermarkJob",
        display = "时间水位线设置",
        description = "在EventTime时间模式下设置watermark，为窗口聚合做准备",
        icon = "WatermarkJob.svg",
        doc = "WatermarkJob.md",
        category = OperatorCategory.WINDOW_OPERATION
)
public class WatermarkJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        DataStream<Map<String, Object>>,
        WatermarkJobConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(WatermarkJob.class);

    @Override
    public DataStream<Map<String, Object>> build(
            FlinkEnvironment context,
            DataStream<Map<String,Object>> dataStream,
            WatermarkJobConfig config,
            JobMetaConfig metaConfig) throws Throwable {

        TimeCharacteristic timeCharacteristic = context.streamEnv.getStreamTimeCharacteristic();
        // Watermark 只有在EventTime模式下才会设置
        if (!timeCharacteristic.equals(TimeCharacteristic.EventTime)) {
            return dataStream;
        }

        TimestampConvertJobConfig convertConfig = new TimestampConvertJobConfig();
        convertConfig.setTimestampField(config.getTimeFieldName());
        convertConfig.setFromFormat(config.getTimeFormat());
        convertConfig.setFromLocale(config.getTimeLocale());
        TimestampConvertUtil convertUtil = new TimestampConvertUtil(convertConfig);

        return dataStream.assignTimestampsAndWatermarks(new WatermarkPeriodicAssigner<Map<String, Object>>(Time.seconds(config.getLagBehindTime()), config) {
            @Override
            public long extractTimestamp(Map<String, Object> element) {
                long timestamp = 0L;

                // 使用数据的时间字段转换获取数据时间
                if (WATERMARK_SOURCE_TIME_FIELD.equals(config.getWatermarkSource())) {
                    DateTime dt = null;
                    try {
                        dt = convertUtil.parseDateTimeFromSource(element.get(config.getTimeFieldName()));
                    } catch (Exception ex) {
                        LOG.error("parseDateTimeFromSource error", ex);
                    }

                    if (dt != null) {
                        timestamp = dt.getMillis();
                    }
                } else { // 使用系统时间
                    timestamp = System.currentTimeMillis();
                }

                return timestamp;
            }
        });
    }

    @Override
    public WatermarkJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        WatermarkJobConfig config = new WatermarkJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
