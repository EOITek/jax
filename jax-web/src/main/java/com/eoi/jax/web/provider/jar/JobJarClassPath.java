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

package com.eoi.jax.web.provider.jar;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.common.VersionUtil;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.dao.entity.TbOptsFlink;
import com.eoi.jax.web.dao.entity.TbOptsSpark;
import com.eoi.jax.web.dao.service.TbOptsFlinkService;
import com.eoi.jax.web.dao.service.TbOptsSparkService;
import com.eoi.jax.web.provider.cluster.ClusterFlinkOpts;
import com.eoi.jax.web.provider.cluster.ClusterSparkOpts;
import com.eoi.jax.web.provider.cluster.ClusterVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobJarClassPath {
    @Autowired
    private TbOptsFlinkService tbOptsFlinkService;

    @Autowired
    private TbOptsSparkService tbOptsSparkService;

    public List<ClassPath> buildClassPaths(String jarPath) throws IOException {
        ClassLoader parentClassLoader = this.getClass().getClassLoader();
        String supportVersion = VersionUtil.loadSupportVersion(jarPath, parentClassLoader);
        boolean maybeSparkJar = maybeSparkJar(jarPath, parentClassLoader);
        List<ClassPath> result = new ArrayList<>();
        if (maybeSparkJar) {
            //如果是spark任务，则优先使用spark的classpath，以保证尽快得到扫描结果
            result.addAll(listSparkClassPaths(jarPath, supportVersion));
            result.addAll(listFlinkClassPaths(jarPath, supportVersion));
        } else {
            //如果不是spark任务，则优先使用flink的classpath，以保证尽快得到扫描结果
            result.addAll(listFlinkClassPaths(jarPath, supportVersion));
            result.addAll(listSparkClassPaths(jarPath, supportVersion));
        }
        // 内部按classPaths去重
        return result.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 推测是否是Spark-Job-Jar
     *
     */
    private boolean maybeSparkJar(String jarPath, ClassLoader classLoader) throws IOException {
        if (StrUtil.containsIgnoreCase(jarPath, "spark")) {
            return true;
        }
        try (URLClassLoader loader = new URLClassLoader(
                new URL[]{new File(jarPath).toURI().toURL()},
                classLoader
        )) {
            try {
                ClassPathResource resource = new ClassPathResource("META-INF/services/com.eoi.jax.api.Builder", loader);
                List<String> lines = IoUtil.readLines(resource.getStream(), StandardCharsets.UTF_8, new ArrayList<>());
                for (String line : lines) {
                    try {
                        Class<?>[] interfaces = loader.loadClass(line).getInterfaces();
                        for (Class<?> clz : interfaces) {
                            if (isSparkJob(clz.getCanonicalName())) {
                                return true;
                            }
                            if (isFlinkJob(clz.getCanonicalName())) {
                                return false;
                            }
                        }
                    } catch (Throwable throwable) {
                        String msg = ExceptionUtil.stacktraceToString(throwable);
                        if (isSparkJob(msg)) {
                            return true;
                        }
                        if (isFlinkJob(msg)) {
                            return false;
                        }
                    }
                }
            } catch (Throwable ignore) {
            }
        }
        return false;
    }

    private boolean isSparkJob(String test) {
        return StrUtil.containsIgnoreCase(test, "SparkSourceJobBuilder")
                || StrUtil.containsIgnoreCase(test, "SparkProcessJobBuilder")
                || StrUtil.containsIgnoreCase(test, "SparkSinkJobBuilder")
                || StrUtil.containsIgnoreCase(test, "SparkSourceTableBuilder")
                || StrUtil.containsIgnoreCase(test, "SparkProcessTableBuilder")
                || StrUtil.containsIgnoreCase(test, "SparkSinkTableBuilder");
    }

    private boolean isFlinkJob(String test) {
        return StrUtil.containsIgnoreCase(test, "FlinkSourceJobBuilder")
                || StrUtil.containsIgnoreCase(test, "FlinkProcessJobBuilder")
                || StrUtil.containsIgnoreCase(test, "FlinkSinkJobBuilder");
    }

    private List<ClassPath> listFlinkClassPaths(String jarPath, String supportVersion) {
        List<ClassPath> classPaths = new ArrayList<>();
        ClusterFlinkOpts opts = new ClusterFlinkOpts().withDefault();
        classPaths.add(new ClassPath("flink-from-config", listAllJars(
                ClusterVariable.genFlinkLib(ClusterVariable.replaceVariable(opts.getHome())),
                ClusterVariable.replaceVariable(opts.getJobLib()),
                jarPath
        )));
        classPaths.addAll(
                tbOptsFlinkService.list().stream()
                        .map(FlinkOptsAdapter::new)
                        .distinct()
                        .sorted()
                        .filter(i -> i.validate(supportVersion))
                        .map(i -> new ClassPath(i.opts.getFlinkOptsName(), listAllJars(
                                ClusterVariable.genFlinkLib(ClusterVariable.replaceVariable(i.opts.getHome())),
                                ClusterVariable.replaceVariable(i.opts.getJobLib()),
                                jarPath
                        )))
                        .collect(Collectors.toList())
        );
        return classPaths;
    }

    private List<ClassPath> listSparkClassPaths(String jarPath, String supportVersion) {
        List<ClassPath> classPaths = new ArrayList<>();
        ClusterSparkOpts opts = new ClusterSparkOpts().withDefault();
        classPaths.add(new ClassPath("spark-from-config", listAllJars(
                ClusterVariable.genSparkLib(ClusterVariable.replaceVariable(opts.getHome())),
                ClusterVariable.replaceVariable(opts.getJobLib()),
                jarPath
        )));
        classPaths.addAll(
                tbOptsSparkService.list().stream()
                        .map(SparkOptsAdapter::new)
                        .distinct()
                        .sorted()
                        .filter(i -> i.validate(supportVersion))
                        .map(i -> new ClassPath(i.opts.getSparkOptsName(), listAllJars(
                                ClusterVariable.genSparkLib(ClusterVariable.replaceVariable(i.opts.getHome())),
                                ClusterVariable.replaceVariable(i.opts.getJobLib()),
                                jarPath
                        )))
                        .collect(Collectors.toList())
        );
        return classPaths;
    }

    private List<String> listAllJars(String runtimeLib, String jobLib, String jarPath) {
        List<String> runLibJars = Common.listJars(runtimeLib);
        List<String> jobLibJars = Common.listJars(jobLib);
        List<String> jars = new ArrayList<>();
        jars.addAll(runLibJars);
        jars.addAll(jobLibJars);
        jars.add(jarPath);
        return jars;
    }

    abstract class OptsAdapter implements Comparable<OptsAdapter> {

        abstract String getVersion();

        abstract String getJobLib();

        abstract String getHome();

        public boolean validate(String supportVersion) {
            return VersionUtil.validate(supportVersion, getVersion());
        }

        @Override
        public int compareTo(OptsAdapter o) {
            return StrUtil.compareVersion(getVersion(), o.getVersion());
        }

        /**
         * 只关注version、jobLib、home字段
         */
        @Override
        public String toString() {
            return String.format("{\"version\":\"%s\",\"jobLib\":\"%s\",\"home\":\"%s\"}",
                    getVersion(),
                    getJobLib(),
                    getHome());
        }

        /**
         * 只关注version、jobLib、home字段
         */
        @Override
        public int hashCode() {
            return (toString()).hashCode();
        }

        /**
         * 只关注version、jobLib、home字段
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof OptsAdapter)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            return this.hashCode() == obj.hashCode();
        }
    }

    class FlinkOptsAdapter extends OptsAdapter {
        TbOptsFlink opts;

        public FlinkOptsAdapter(TbOptsFlink opts) {
            this.opts = opts;
        }

        @Override String getVersion() {
            return opts.getVersion();
        }

        @Override String getJobLib() {
            return opts.getJobLib();
        }

        @Override String getHome() {
            return opts.getHome();
        }
    }

    class SparkOptsAdapter extends OptsAdapter {
        TbOptsSpark opts;

        public SparkOptsAdapter(TbOptsSpark opts) {
            this.opts = opts;
        }

        @Override String getVersion() {
            return opts.getVersion();
        }

        @Override String getJobLib() {
            return opts.getJobLib();
        }

        @Override String getHome() {
            return opts.getHome();
        }
    }
}
