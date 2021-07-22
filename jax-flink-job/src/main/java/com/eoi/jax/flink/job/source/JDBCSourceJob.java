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

package com.eoi.jax.flink.job.source;

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkSourceJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

@Job(
        name = "JDBCSourceJob",
        display = "JDBC数据源",
        description = "通过jdbc定时执行查询sql语句获取源数据",
        doc = "JDBCSourceJob.md",
        icon = "JDBCSourceJob.svg"
)
public class JDBCSourceJob implements FlinkSourceJobBuilder<DataStream<Map<String, Object>>, JDBCSourceJobConfig> {
    @Override
    public DataStream<Map<String, Object>> build(FlinkEnvironment context,
                                                 JDBCSourceJobConfig config,
                                                 JobMetaConfig metaConfig) throws Throwable {
        return context.streamEnv.addSource(new JDBCSourceFunction(config))
                .setParallelism(1)
                .name(metaConfig.getJobEntry()).uid(metaConfig.getJobId())
                .rebalance();
    }

    @Override
    public JDBCSourceJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        JDBCSourceJobConfig config = new JDBCSourceJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
