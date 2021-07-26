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

package com.eoi.jax.flink1_12.job.sink;

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkSinkJobBuilder;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.flink.job.common.AdvanceConfig;
import com.eoi.jax.flink.job.common.ElasticSearchSinkJobConfig;
import com.eoi.jax.flink1_12.job.common.ElasticSearchRestClientConfigFactory;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkBase;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;
import org.apache.flink.streaming.connectors.elasticsearch7.RestClientFactory;

import java.io.IOException;
import java.util.Map;

@Job(
        name = "ElasticSearchSinkJob",
        display = "Elasticsearch_1.12",
        description = "该job是来配置es写入的flink job",
        icon = "ElasticSearchSinkJob.svg",
        doc = "ElasticSearchSinkJob.md"
)
public class ElasticSearchSinkJob extends AdvanceConfig implements FlinkSinkJobBuilder<DataStream<Map<String, Object>>, ElasticSearchSinkJobConfig> {
    @Override
    public void build(FlinkEnvironment context, DataStream<Map<String, Object>> mapDataStream, ElasticSearchSinkJobConfig config, JobMetaConfig metaConfig)
            throws JobConfigValidationException {
        ElasticSearchMapSinkFunction function = new ElasticSearchMapSinkFunction(
                config.getIndexPattern(),
                config.getIndexType(),
                config.getDocIdField(),
                config.getTimeFieldName(),
                config.getTimeFormat(),
                config.getTimeLocale());

        ElasticsearchSink.Builder builder = new ElasticsearchSink.Builder(config.getEsNodesHttpHost(), function);
        builder.setBulkFlushMaxActions(config.getBulkActions());
        builder.setBulkFlushMaxSizeMb(config.getBulkSize());
        builder.setBulkFlushInterval(config.getFlushInterval());
        builder.setBulkFlushBackoff(config.isFlushBackOffEnable());
        builder.setBulkFlushBackoffType(ElasticsearchSinkBase.FlushBackoffType.valueOf(config.getBackoffType()));
        builder.setBulkFlushBackoffRetries(config.getBackoffRetries());
        builder.setBulkFlushBackoffDelay(config.getBackoffDelay());
        builder.setFailureHandler(new ElasticsearchFailureHandler(config.getShouldThrowExMessages()));

        RestClientFactory factory = new ElasticSearchRestClientConfigFactory(
                config.getRequestTimeout(),
                config.getHeaderJson(),
                config.isAuthEnable(),
                config.getAuthUser(),
                config.getAuthPassword(),
                config.getKerberosKeytab(),
                config.getKerberosPrincipal()
        );
        builder.setRestClientFactory(factory);

        ElasticsearchSink sink = builder.build();
        setAdvanceConfig(mapDataStream.addSink(sink).name(metaConfig.getJobEntry()).uid(metaConfig.getJobId()), metaConfig.getOpts());
    }

    @Override
    public ElasticSearchSinkJobConfig configure(Map<String, Object> mapConfig) throws IOException {
        ElasticSearchSinkJobConfig config = new ElasticSearchSinkJobConfig(mapConfig);
        return config;
    }
}
