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

package com.eoi.jax.flink1_12.job.common;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.common.JsonUtil;
import org.apache.flink.annotation.VisibleForTesting;
import org.apache.flink.streaming.connectors.elasticsearch7.RestClientFactory;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClientBuilder;

import java.util.Map;

public class ElasticSearchRestClientConfigFactory implements RestClientFactory {
    int requestTimeout;
    String headerJson;
    boolean authEnable;
    String authUser;
    String authPassword;
    private String keytab;
    private String principal;

    public ElasticSearchRestClientConfigFactory(int requestTimeout, String headerJson, boolean authEnable, String authUser, String authPassword, String keytab, String principal) {
        this.requestTimeout = requestTimeout;
        this.headerJson = headerJson;
        this.authEnable = authEnable;
        this.authUser = authUser;
        this.authPassword = authPassword;
        this.keytab = keytab;
        this.principal = principal;
    }

    private Header[] decodeHeaders() {
        try {
            Map<String, Object> headerMap = JsonUtil.decode2Map(this.headerJson);
            Header[] headers = new BasicHeader[headerMap.size()];
            int tempI = 0;
            for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
                headers[tempI] = new BasicHeader(entry.getKey(), entry.getValue().toString());
                tempI++;
            }
            return headers;
        } catch (Exception ex) {
            return null;
        }
    }

    @VisibleForTesting
    public Header[] getHeaders() {
        if (StrUtil.isEmpty(headerJson)) {
            return null;
        }

        return decodeHeaders();
    }

    @Override
    public void configureRestClientBuilder(RestClientBuilder restClientBuilder) {
        Header[] headers = getHeaders();
        if (headers != null) {
            restClientBuilder.setDefaultHeaders(headers);
        }

        ElasticSearchHttpClientConfigCallback configCallback = new ElasticSearchHttpClientConfigCallback(keytab,principal,authUser,authPassword);

        restClientBuilder.setRequestConfigCallback(new ElasticSearchRequestConfigCallback())
                .setHttpClientConfigCallback(configCallback);

    }

    class ElasticSearchRequestConfigCallback implements RestClientBuilder.RequestConfigCallback {
        @Override
        public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
            return builder.setSocketTimeout(requestTimeout).setConnectTimeout(requestTimeout);
        }
    }

}
