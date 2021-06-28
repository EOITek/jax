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

package com.eoi.jax.web.provider.manager;

import com.eoi.jax.manager.JaxManager;
import com.eoi.jax.manager.flink.FlinkManager;
import com.eoi.jax.manager.spark.SparkManager;
import com.eoi.jax.manager.yarn.YarnManager;
import com.eoi.jax.web.provider.ProcessRunnerProvider;
import com.eoi.jax.web.provider.cluster.Cluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JaxManagerProvider {
    @Autowired
    private ProcessRunnerProvider processRunnerProvider;

    @Autowired
    private ConsoleLogLineQueue consoleLogLineQueue;

    public JaxManager flinkCmdJaxManager(String cmd, Cluster cluster) {
        return new FlinkManager()
                .setRunner(processRunnerProvider.flinkCmdProcessRunner(cmd, cluster))
                .setHandler(consoleLogLineQueue);
    }

    public JaxManager flinkJaxManager(Cluster cluster) {
        return new FlinkManager()
                .setRunner(processRunnerProvider.flinkProcessRunner(cluster))
                .setHandler(consoleLogLineQueue);
    }

    public JaxManager sparkJaxManager(Cluster cluster) {
        return new SparkManager()
                .setRunner(processRunnerProvider.sparkProcessRunner(cluster))
                .setHandler(consoleLogLineQueue);
    }

    public JaxManager yarnJaxManager(Cluster cluster) {
        return new YarnManager()
                .setRunner(processRunnerProvider.yarnProcessRunner(cluster));
    }
}
