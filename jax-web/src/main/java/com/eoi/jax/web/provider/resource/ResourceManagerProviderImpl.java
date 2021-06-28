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

package com.eoi.jax.web.provider.resource;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.client.FlinkOverview;
import com.eoi.jax.web.client.FlinkRestClient;
import com.eoi.jax.web.client.SparkOverview;
import com.eoi.jax.web.client.SparkRestClient;
import com.eoi.jax.web.client.YarnOverview;
import com.eoi.jax.web.client.YarnRestClient;
import com.eoi.jax.web.common.consts.ClusterType;
import com.eoi.jax.web.common.util.HadoopUtil;
import com.eoi.jax.web.provider.ResourceManagerProvider;
import com.eoi.jax.web.provider.cluster.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceManagerProviderImpl implements ResourceManagerProvider {
    private static final Logger logger = LoggerFactory.getLogger(ResourceManagerProviderImpl.class);

    @Override
    public ClusterResourcePool getResourcePool(Cluster cluster) {
        ClusterResourcePool pool = new ClusterResourcePool();
        pool.setClusterName(cluster.getClusterName());
        pool.setClusterType(cluster.getClusterType());
        try {
            if (ClusterType.YARN.isEqual(cluster.getClusterType())) {
                List<String> urls = HadoopUtil.readAllYarnWebUrls(cluster.getHadoopConfig());
                YarnRestClient client =  new YarnRestClient(urls);
                YarnOverview yarnOverview = client.getClusterMetric();
                pool.setYarn(yarnOverview).setYarnGot(true);
            }
            if (ClusterType.FLINK_STANDALONE.isEqual(cluster.getClusterType())) {
                FlinkRestClient client =  new FlinkRestClient(genHttpUrl(cluster.getFlinkWebUrl()));
                FlinkOverview overview = client.getOverview();
                pool.setFlink(overview).setFlinkGot(true);
            }
            if (ClusterType.SPARK_STANDALONE.isEqual(cluster.getClusterType())) {
                SparkRestClient client =  new SparkRestClient(genHttpUrl(cluster.getSparkWebUrl()));
                SparkOverview overview = client.getOverview();
                pool.setSpark(overview).setSparkGot(true);
            }
        } catch (Exception e) {
            logger.warn("query resource error", e);
        }
        return pool;
    }

    private String genHttpUrl(String str) {
        if (StrUtil.startWith(str, "http://") || StrUtil.startWith(str, "https://")) {
            return str;
        }
        return "http://" + str;
    }
}
