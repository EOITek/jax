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

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.eoi.jax.common.PythonHelper;
import com.eoi.jax.web.common.config.AppConfig;
import com.eoi.jax.web.common.config.ConfigLoader;
import com.eoi.jax.web.common.exception.JaxException;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.model.manager.FileUrl;
import com.eoi.jax.web.model.manager.JobJar;
import com.eoi.jax.web.model.manager.PipelineJar;
import com.eoi.jax.web.provider.JarFileProvider;
import com.eoi.jax.web.provider.cluster.Cluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JarUrlProvider {
    @Autowired
    private JarFileProvider jarFileProvider;

    public String getListeningHttp() {
        try {
            return ConfigLoader.load().server.getListenHttp();
        } catch (Exception e) {
            throw new JaxException(e);
        }
    }

    public List<FileUrl> getJobJarUrls(List<JobJar> jarList, List<PipelineJar> extJars) {
        List<String> jarPaths = jarList.stream()
                .filter(jar -> !jar.getJob().getJobName().startsWith(PythonHelper.PYTHON_VIRTUAL_PACKAGE_SPARK)
                        && !jar.getJob().getJobName().startsWith(PythonHelper.PYTHON_VIRTUAL_PACKAGE_FLINK))
                .map(jar -> jarFileProvider.touchJarFile(jar.getJar(), jar.getCluster()))
                .collect(Collectors.toList());
        List<String> extPaths = extJars.stream()
                .map(jar -> jarFileProvider.touchJarFile(jar.getJar(), jar.getCluster()))
                .collect(Collectors.toList());
        jarPaths.addAll(extPaths);
        return jarPaths.stream().distinct().map(this::genJarDirUrl).collect(Collectors.toList());
    }

    public List<FileUrl> getJobLibUrls(String jobLib) {
        return Common.listJars(jobLib).stream().map(this::genJarLibUrl).collect(Collectors.toList());
    }

    public String getJobPyArchiveUrl(List<JobJar> jarList) {
        Set<String> jarPythons = jarList.stream()
                .filter(jar -> jar.getJob().getJobName().startsWith(PythonHelper.PYTHON_VIRTUAL_PACKAGE_FLINK))
                .map(jar -> jar.getJar().getJarName())
                .collect(Collectors.toSet());
        return String.format("%s/api/v1/jar-archive?jars=%s",
                getListeningHttp(),
                URLUtil.encode(String.join(",", jarPythons))
        );
    }

    public List<FileUrl> getJobPyFileUrls(List<JobJar> jarList) {
        List<String> jarPaths = jarList.stream()
                .filter(jar -> jar.getJob().getJobName().startsWith(PythonHelper.PYTHON_VIRTUAL_PACKAGE_SPARK))
                .map(jar -> jarFileProvider.touchJarFile(jar.getJar(), jar.getCluster()))
                .collect(Collectors.toList());
        return jarPaths.stream().distinct().map(this::genJarDirUrl).collect(Collectors.toList());
    }

    public List<FileUrl> genPythonLibUrl(Cluster cluster) {
        List<String> pyFilePaths = Arrays.stream(FileUtil.ls(ConfigLoader.load().jax.getJaxPythonDir()))
                .filter(file -> FileUtil.getName(file).endsWith(".zip"))
                .map(FileUtil::getAbsolutePath)
                .collect(Collectors.toList());
        return pyFilePaths.stream().distinct().map(this::genPyDirUrl).collect(Collectors.toList());
    }

    public FileUrl genWorkUrl(String filePath) {
        return genFileUrl(ConfigLoader.load().jax.getWork(), filePath, AppConfig.JAX_JOB_WORK_DIR);
    }

    public FileUrl genJarLibUrl(String filePath) {
        return genFileUrl(ConfigLoader.load().jax.getJarLib(), filePath, AppConfig.JAX_JOB_JAR_LIB);
    }

    public FileUrl genJarDirUrl(String filePath) {
        return genFileUrl(ConfigLoader.load().jax.getJarDir(), filePath, AppConfig.JAX_JOB_JAR_DIR);
    }

    public FileUrl genPyDirUrl(String filePath) {
        return genFileUrl(ConfigLoader.load().jax.getJaxPythonDir(), filePath, AppConfig.JAX_JOB_PYTHON_DIR);
    }

    private FileUrl genFileUrl(String dirPath, String filePath, String httpBase) {
        if (!FileUtil.exist(filePath)) {
            throw new JaxException("the file " + filePath + " is not exist");
        }
        if (!StrUtil.startWith(filePath, dirPath)) {
            throw new JaxException("the file " + filePath + " is not in dictionary " + dirPath);
        }
        String relativePath = StrUtil.unWrap(StrUtil.removePrefix(filePath, dirPath), '/');
        String url = Common.urlPathsJoin(getListeningHttp(), httpBase, relativePath);
        String name = FileUtil.getName(filePath);
        return new FileUrl().setName(name).setUrl(url).setPath(filePath);
    }
}
