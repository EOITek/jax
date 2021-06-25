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

package com.eoi.jax.manager.yarn;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.manager.exception.InvalidParamException;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JaxYarnClient {
    private static final Logger logger = LoggerFactory.getLogger(JaxYarnClient.class);

    public static YarnAppReport getApplicationReport(final JaxYarnParam param) throws Exception {
        return doAction(param, new JaxYarnAction<YarnAppReport>() {
            @Override
            public YarnAppReport run(YarnClient yarnClient, JaxYarnParam param) throws Exception {
                ApplicationReport report = yarnClient.getApplicationReport(getApplicationId(param.getApplicationId()));
                return YarnAppReport.fromReport(report);
            }
        });
    }

    public static List<YarnAppReport> listApplicationReport(final JaxYarnParam param) throws Exception {
        return doAction(param, new JaxYarnAction<List<YarnAppReport>>() {
            @Override
            public List<YarnAppReport> run(YarnClient yarnClient, JaxYarnParam param) throws Exception {
                List<ApplicationReport> reports = yarnClient.getApplications();
                return reports.stream().map(YarnAppReport::fromReport).collect(Collectors.toList());
            }
        });
    }

    public static void killApplication(final JaxYarnParam param) throws Exception {
        doAction(param, new JaxYarnAction<Object>() {
            @Override
            public Object run(YarnClient yarnClient, JaxYarnParam param) throws Exception {
                yarnClient.killApplication(getApplicationId(param.getApplicationId()));
                return null;
            }
        });
    }

    public static <T> T doAction(JaxYarnParam param, JaxYarnAction<T> action) throws Exception {
        try (YarnClient yarnClient = createYarnClient(param.getHadoopConfDir())) {
            if (StrUtil.isNotEmpty(param.getPrincipal()) && StrUtil.isNotEmpty(param.getKeytab())) {
                UserGroupInformation ugi = loginKerberos(param.getPrincipal(), param.getKeytab());
                return ugi.doAs(new PrivilegedExceptionAction<T>() {
                    @Override
                    public T run() throws Exception {
                        yarnClient.start();
                        return doActionWithTimeout(param, action, yarnClient);

                    }
                });
            } else {
                yarnClient.start();
                return doActionWithTimeout(param, action, yarnClient);
            }
        }
    }

    private static <T> T doActionWithTimeout(JaxYarnParam param, JaxYarnAction<T> action, YarnClient yarnClient) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<T> future = executor.submit(() ->
                    action.run(yarnClient, param)
            );
            return future.get(param.getTimeOutMs(), TimeUnit.MILLISECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    public static YarnClient createYarnClient(String hadoopConfDir) {
        YarnClient yarnClient = YarnClient.createYarnClient();
        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        initYarnConfiguration(yarnConfiguration, hadoopConfDir);
        UserGroupInformation.setConfiguration(yarnConfiguration);
        yarnClient.init(yarnConfiguration);
        //yarnClient.start();
        return yarnClient;
    }

    private static void initYarnConfiguration(YarnConfiguration yarnConfiguration, String hadoopConfDir) {
        try {
            File dir = new File(hadoopConfDir);
            File[] files = FileUtil.listFiles(dir);
            for (File file: files) {
                if (file.isFile() && file.canRead() && file.getName().endsWith(".xml")) {
                    yarnConfiguration.addResource(new Path(file.getAbsolutePath()));
                }
            }
        } catch (Exception ex) {
            throw new InvalidParamException("SET HADOOP CONF ERROR", ex);
        }
    }

    private static UserGroupInformation loginKerberos(String principal, String keytab) throws IOException {
        if (StrUtil.isEmpty(principal) || StrUtil.isEmpty(keytab)) {
            throw new InvalidParamException("miss principal or keytab");
        }
        return UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal, keytab);
    }

    private static ApplicationId getApplicationId(String applicationId) {
        if (StrUtil.isEmpty(applicationId)) {
            throw new InvalidParamException("miss applicationId");
        }
        return ConverterUtils.toApplicationId(applicationId);
    }

    public interface JaxYarnAction<T> {
        T run(YarnClient yarnClient, JaxYarnParam param) throws Exception;
    }
}
