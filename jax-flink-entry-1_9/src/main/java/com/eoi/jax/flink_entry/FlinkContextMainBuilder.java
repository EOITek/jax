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

package com.eoi.jax.flink_entry;

import cn.hutool.core.bean.BeanUtil;
import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkSqlJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.common.JsonUtil;
import com.eoi.jax.core.FlinkJobDAGBuilder;
import com.eoi.jax.core.FlinkJobNode;
import com.eoi.jax.core.FlinkJobOpts;
import com.eoi.jax.core.JobBuildException;
import com.eoi.jax.core.entry.EdgeDescription;
import com.eoi.jax.core.entry.JobDescription;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.java.StreamTableEnvironment;

import java.util.HashMap;
import java.util.Map;

public class FlinkContextMainBuilder {

    private FlinkContextMainBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static FlinkPipelineDescription description(String jobJson) throws Throwable {
        // 参数初始化
        FlinkPipelineDescription flinkPipelineDescription = JsonUtil.decode(jobJson, FlinkPipelineDescription.class);

        flinkPipelineDescription.validate(isSqlJob(flinkPipelineDescription));
        flinkPipelineDescription.ensureDefaults();
        return flinkPipelineDescription;
    }

    public static FlinkEnvironment build(FlinkPipelineDescription flinkPipelineDescription) throws Throwable {
        // init flink env
        StreamExecutionEnvironment streamEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        return build(streamEnv, flinkPipelineDescription);
    }

    public static FlinkEnvironment build(StreamExecutionEnvironment streamEnv, FlinkPipelineDescription flinkPipelineDescription) throws Throwable {
        buildTimeCharacteristic(streamEnv, flinkPipelineDescription);
        buildCheckpoint(streamEnv, flinkPipelineDescription);
        buildStateBackend(streamEnv, flinkPipelineDescription);
        buildGlobalJobParameters(streamEnv, flinkPipelineDescription);
        StreamTableEnvironment tableEnv = buildTableEnvironment(streamEnv, flinkPipelineDescription);
        return buildDAG(streamEnv, tableEnv, flinkPipelineDescription);
    }

    public static void buildTimeCharacteristic(StreamExecutionEnvironment streamEnv, FlinkPipelineDescription flinkPipelineDescription) {
        TimeCharacteristic timeCharacteristicSetting = TimeCharacteristic.EventTime;
        if (!"event".equalsIgnoreCase(flinkPipelineDescription.getTimeCharacteristic())) {
            timeCharacteristicSetting = TimeCharacteristic.ProcessingTime;
        }
        streamEnv.setStreamTimeCharacteristic(timeCharacteristicSetting);
    }

    public static void buildCheckpoint(StreamExecutionEnvironment streamEnv, FlinkPipelineDescription flinkPipelineDescription) {
        // checkpointing设置
        if (flinkPipelineDescription.getCheckpointInterval() > 0) {
            streamEnv.enableCheckpointing(flinkPipelineDescription.getCheckpointInterval());
            streamEnv.getCheckpointConfig().setMinPauseBetweenCheckpoints(10000);
        }
    }

    public static void buildStateBackend(StreamExecutionEnvironment streamEnv, FlinkPipelineDescription flinkPipelineDescription) throws Exception {
        // set backend
        if ("rocksdb".equalsIgnoreCase(flinkPipelineDescription.getBackend())) {
            RocksDBStateBackend rocksdb = new RocksDBStateBackend(flinkPipelineDescription.getCheckpointURI(), true);
            rocksdb.setDbStoragePaths(flinkPipelineDescription.getRocksDbPath());
            streamEnv.setStateBackend(rocksdb);
        } else if ("fs".equalsIgnoreCase(flinkPipelineDescription.getBackend())) {
            streamEnv.setStateBackend(new FsStateBackend(flinkPipelineDescription.getCheckpointURI()));
        } else {
            streamEnv.setStateBackend(new MemoryStateBackend());
        }
    }

    public static void buildGlobalJobParameters(StreamExecutionEnvironment streamEnv, FlinkPipelineDescription flinkPipelineDescription) throws Exception {
        if (Boolean.TRUE.equals(flinkPipelineDescription.getDisableOperatorChaining())) {
            streamEnv.disableOperatorChaining();
        }
        streamEnv.getConfig().setGlobalJobParameters(flinkPipelineDescription.toFlinkParameter());
    }

    public static StreamTableEnvironment buildTableEnvironment(StreamExecutionEnvironment streamEnv, FlinkPipelineDescription flinkPipelineDescription) {
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(streamEnv, settings);
        tableEnv.getConfig().setIdleStateRetentionTime(
                Time.hours(flinkPipelineDescription.getMinIdleStateRetentionTime()),
                Time.hours(flinkPipelineDescription.getMaxIdleStateRetentionTime())
        );
        return tableEnv;
    }

    public static final String SQL_EXECUTE_JOB = "com.eoi.jax.flink1_12.job.process.SqlExecuteJob";

    public static FlinkEnvironment buildDAG(
            StreamExecutionEnvironment streamEnv,
            StreamTableEnvironment tableEnv,
            FlinkPipelineDescription flinkPipelineDescription) throws Throwable {
        // 组装DAG
        FlinkEnvironment context = new FlinkEnvironment(streamEnv, tableEnv);

        // 如果是纯sql作业的pipeline，就只有一个SqlExecuteJob
        // 无需构建DAG，直接调用FlinkSqlJobBuilder的build方法
        if (isSqlJob(flinkPipelineDescription)) {
            JobDescription jobDescription = flinkPipelineDescription.getJobs().get(0);
            Object jobBuilder;
            try {
                jobBuilder = Class.forName(SQL_EXECUTE_JOB).newInstance();
            } catch (Exception ex) {
                throw new JobBuildException("failed to create instance of class " + SQL_EXECUTE_JOB, ex);
            }
            FlinkSqlJobBuilder sqlJobBuilder = (FlinkSqlJobBuilder)jobBuilder;
            Object config = sqlJobBuilder.configure(jobDescription.getConfig());
            JobMetaConfig metaConfig = new JobMetaConfig(jobDescription.getId(), jobDescription.getEntry(), flinkPipelineDescription.getPipelineName());
            sqlJobBuilder.build(context, config, metaConfig);
        } else {
            FlinkJobDAGBuilder jobBuilder = new FlinkJobDAGBuilder(context, flinkPipelineDescription.getPipelineName());
            Map<String, FlinkJobNode> jobNodeMap = new HashMap<>();

            for (JobDescription job : flinkPipelineDescription.getJobs()) {
                FlinkJobNode jobNode = new FlinkJobNode();
                jobNode.setId(job.getId());
                jobNode.setEntry(job.getEntry());
                jobNode.setConfig(job.getConfig());
                jobNode.setOpts(BeanUtil.mapToBean(job.getOpts(), FlinkJobOpts.class, true));
                jobNodeMap.put(job.getId(), jobNode);
            }
            // put edges
            for (EdgeDescription edge : flinkPipelineDescription.getEdges()) {
                String from = edge.getFrom();
                String to = edge.getTo();
                Integer toSlot = edge.getToSlot();
                Integer fromSlot = edge.getFromSlot();

                if (jobNodeMap.containsKey(from) && jobNodeMap.containsKey(to)) {
                    if (null != fromSlot && null != toSlot) {
                        jobBuilder.putEdge(jobNodeMap.get(from), jobNodeMap.get(to), fromSlot, toSlot);
                    } else if (null != toSlot) {
                        jobBuilder.putEdge(jobNodeMap.get(from), jobNodeMap.get(to), toSlot);
                    } else {
                        jobBuilder.putEdge(jobNodeMap.get(from), jobNodeMap.get(to));
                    }
                }
            }

            // start to visit and build
            jobBuilder.build();
        }


        return context;
    }

    public static boolean isSqlJob(FlinkPipelineDescription flinkPipelineDescription) {
        return Boolean.TRUE.equals(flinkPipelineDescription.getSqlJob())
                || (flinkPipelineDescription.getJobs().size() == 1
                        && SQL_EXECUTE_JOB.equals(flinkPipelineDescription.getJobs().get(0).getEntry()));
    }
}
