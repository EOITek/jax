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

public class SparkRestClient {
    public final String server;

    public SparkRestClient(String server) {
        this.server = server;
    }

    public SparkOverview getOverview() {
        String url = Common.urlPathsJoin(server, "/json/");
        HttpResponse httpResponse = HttpRequest.get(url).contentType("application/json").timeout(5000).execute();
        if (!httpResponse.isOk()) {
            throw new JaxException("http code " + httpResponse.getStatus());
        }
        String respBody = httpResponse.body();
        SparkOverview overview = JsonUtil.decode(respBody, SparkOverview.class);
        overview.setAppsRunning(overview.getActiveApps() == null ? null : overview.getActiveApps().size());
        overview.setDriversDunning(overview.getActiveDrivers() == null ? null : overview.getActiveDrivers().size());
        return overview;
    }
}
