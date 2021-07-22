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

package com.eoi.jax.flink.job.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.core.FlinkJobOpts;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import java.util.Map;

public class AdvanceConfig {
    protected DataStreamSink<?> setAdvanceConfig(DataStreamSink<?> sink, Map<String, Object> opts) {
        FlinkJobOpts flinkJobOpts = BeanUtil.fillBeanWithMap(opts, new FlinkJobOpts(), true);

        if (StrUtil.isNotEmpty(flinkJobOpts.getSlotSharingGroup())) {
            sink = sink.slotSharingGroup(flinkJobOpts.getSlotSharingGroup());
        }
        if (StrUtil.isNotEmpty(flinkJobOpts.getChainingStrategy())) {
            if ("NEVER".equals(flinkJobOpts.getChainingStrategy().toUpperCase())) {
                sink = sink.disableChaining();
            }
        }
        if (flinkJobOpts.getParallelism() != null) {
            sink = sink.setParallelism(flinkJobOpts.getParallelism());
        }
        return sink;
    }

    protected SingleOutputStreamOperator setAdvanceConfig(SingleOutputStreamOperator output,  Map<String, Object> optsMap) {
        FlinkJobOpts opts = BeanUtil.fillBeanWithMap(optsMap, new FlinkJobOpts(), true);
        if (StrUtil.isNotEmpty(opts.getSlotSharingGroup())) {
            output = output.slotSharingGroup(opts.getSlotSharingGroup());
        }
        if (StrUtil.isNotEmpty(opts.getChainingStrategy())) {
            if ("NEVER".equals(opts.getChainingStrategy().toUpperCase())) {
                output = output.disableChaining();
            }
            if ("HEAD".equals(opts.getChainingStrategy().toUpperCase())) {
                output = output.startNewChain();
            }
        }
        if (opts.getBufferTimeout() != null) {
            output = output.setBufferTimeout(opts.getBufferTimeout());
        }
        if (opts.getParallelism() != null) {
            output = output.setParallelism(opts.getParallelism());
        }
        if (opts.getMaxParallelism() != null) {
            output = output.setMaxParallelism(opts.getMaxParallelism());
        }

        return output;
    }
}
