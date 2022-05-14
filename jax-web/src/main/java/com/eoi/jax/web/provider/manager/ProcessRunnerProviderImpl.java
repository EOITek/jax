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

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.manager.process.IProcessRunner;
import com.eoi.jax.manager.process.ProcessRunner;
import com.eoi.jax.web.common.config.AppConfig;
import com.eoi.jax.web.common.config.ConfigLoader;
import com.eoi.jax.web.provider.ProcessRunnerProvider;
import com.eoi.jax.web.provider.cluster.Cluster;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class ProcessRunnerProviderImpl implements ProcessRunnerProvider {

    @Override
    public IProcessRunner flinkCmdProcessRunner(String cmd, Cluster cluster) {
        return new ProcessRunner(
                cluster.getFlinkOpts().getHome(),
                cluster.getFlinkOpts().getBin(),
                cluster.getTimeoutMs()
        ).putEnvironment(
                AppConfig.HADOOP_CONF_DIR,
                cluster.getHadoopConfig(),
                StrUtil.isNotBlank(cluster.getHadoopHome())
        );
    }

    @Override
    public IProcessRunner flinkProcessRunner(Cluster cluster) {
        ProcessRunner processRunner = new ProcessRunner(
                ConfigLoader.load().jax.getWork(),
                cluster.getFlinkOpts().getBin(),
                cluster.getTimeoutMs()
        );
        processRunner.putEnvironment(
                AppConfig.HADOOP_CONF_DIR,
                cluster.getHadoopConfig(),
                StrUtil.isNotBlank(cluster.getHadoopHome())
        );
        // Set env: FLINK_CONF_DIR: 优先取自定flink/conf, 再尝试系统环境变量
        String confDir = cluster.getFlinkOpts().getDefaultConfDir();
        if (Files.exists(Paths.get(confDir))) {
            processRunner.putEnvironment("FLINK_CONF_DIR", confDir, true);
        }
        return processRunner;
    }

    @Override
    public IProcessRunner sparkProcessRunner(Cluster cluster) {
        return new ProcessRunner(
                ConfigLoader.load().jax.getWork(),
                cluster.getSparkOpts().getBin(),
                cluster.getTimeoutMs()
        ).putEnvironment(
                AppConfig.HADOOP_CONF_DIR,
                cluster.getHadoopConfig(),
                StrUtil.isNotBlank(cluster.getHadoopHome())
        );
    }

    @Override
    public IProcessRunner yarnProcessRunner(Cluster cluster) {
        return new ProcessRunner(
                ConfigLoader.load().jax.getWork(),
                cluster.getYarnBin(),
                cluster.getTimeoutMs()
        ).putEnvironment(
                AppConfig.HADOOP_CONF_DIR,
                cluster.getHadoopConfig(),
                StrUtil.isNotBlank(cluster.getHadoopHome())
        );
    }

    @Override
    public IProcessRunner jaxToolRunner(Map<String, String> environment) {
        return new ProcessRunner(
                ConfigLoader.load().jax.getWork(),
                ConfigLoader.load().jax.getTool().getJaxToolBin(),
                ConfigLoader.load().jax.getTool().getJaxToolTimeoutMillis()
        ).setEnvironment(environment);
    }
}
