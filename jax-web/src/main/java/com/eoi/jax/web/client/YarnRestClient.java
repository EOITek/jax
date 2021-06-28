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

package com.eoi.jax.web.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.eoi.jax.web.common.exception.JaxException;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class YarnRestClient {
    private static final Logger logger = LoggerFactory.getLogger(YarnRestClient.class);

    public final List<String> servers;

    public YarnRestClient(List<String> servers) {
        this.servers = servers;
    }

    public YarnOverview getClusterMetric() {
        YarnOverview yarnOverview = null;
        for (String server : servers) {
            try {
                yarnOverview = getClusterMetric(server);
            } catch (Exception ex) {
                logger.warn(String.format("Failed to fetch yarn cluster metrics from %s", server), ex);
            }
            if (yarnOverview != null) {
                return yarnOverview;
            }
        }
        throw new JaxException("Failed to fetch yarn cluster metrics");
    }

    public YarnOverview getClusterMetric(String server) {
        String url = Common.urlPathsJoin(server, "/ws/v1/cluster/metrics");
        HttpResponse httpResponse = HttpRequest.get(url).contentType("application/json").timeout(5000).execute();
        if (!httpResponse.isOk()) {
            throw new JaxException("http code " + httpResponse.getStatus());
        }
        String respBody = httpResponse.body();
        YarnClusterMetric metric = JsonUtil.decode(respBody, YarnClusterMetric.class);
        return metric.getClusterMetrics();
    }
}
