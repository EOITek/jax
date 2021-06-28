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

package com.eoi.jax.web.provider.scanner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.manager.process.ProcessOutput;
import com.eoi.jax.web.common.exception.JaxException;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.common.util.UClassloader;
import com.eoi.jax.web.provider.JaxScanner;
import com.eoi.jax.web.provider.ProcessRunnerProvider;
import com.eoi.jax.web.provider.jar.ClassPath;
import com.eoi.jax.web.provider.jar.JobJarClassPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class JaxScannerImpl implements JaxScanner {
    private static final Logger logger = LoggerFactory.getLogger(JaxScannerImpl.class);

    public static final String PYTHON = "python";
    public static final String JAVA = "java";
    public static final String JAX_TOOL_CLASS_PATH = "JAX_TOOL_CLASS_PATH";

    @Autowired
    private ProcessRunnerProvider processRunnerProvider;

    @Autowired
    private JobJarClassPath jobJarClassPath;

    @Override
    public ScanResult scanJar(String path) {
        return scanByMode(JAVA, path);
    }

    @Override
    public ScanResult scanPython(String path) {
        return scanByMode(PYTHON, path);
    }

    private ScanResult scanByMode(String mode, String path) {
        List<ClassPath> classPaths;
        try {
            classPaths = jobJarClassPath.buildClassPaths(path);
        } catch (IOException e) {
            throw new JaxException("scan error", e);
        }
        for (ClassPath classPath : classPaths) {
            try {
                logger.info("scan job from {}", classPath.toString());
                return scanByClassPath(mode, path, classPath);
            } catch (Exception e) {
                logger.warn("scan job failed from {}", classPath.toString(), e);
            }
        }
        throw new JaxException("no job found");
    }

    private ScanResult scanByClassPath(String mode, String path, ClassPath classPath) {
        String output = path + ".output";
        Map<String, String> env = Collections.singletonMap(JAX_TOOL_CLASS_PATH, classPath.genClassPath());
        List<String> args = genArgs(mode, path, output);
        ProcessOutput processOutput = processRunnerProvider.jaxToolRunner(env).exec(args, logger::info);
        if (processOutput.getCode() != 0) {
            safeDelete(output);
            throw new JaxException("scan failed without success code 0");
        }
        String content = FileUtil.readString(output, StandardCharsets.UTF_8);
        safeDelete(output);
        ScanResult result = JsonUtil.decode(content, ScanResult.class);
        if (!Boolean.TRUE.equals(result.getSuccess())) {
            logger.error("scan failed: {}", result.getStackTrace());
            throw new JaxException("scan failed: " + result.getMessage());
        }
        if (JAVA.equals(mode)) {
            readDocIcon(path, result);
        }
        return result;
    }

    private ScanResult readDocIcon(String path, ScanResult result) {
        if (CollUtil.isEmpty(result.getJobs())) {
            return result;
        }
        try (UClassloader loader = new UClassloader(
                new File(path).toURI().toURL(),
                this.getClass().getClassLoader()
        )) {
            for (JobMeta meta : result.getJobs().values()) {
                if (meta.getJobInfo() == null) {
                    continue;
                }
                if (StrUtil.isNotEmpty(meta.getJobInfo().getDoc())) {
                    String resPath = String.format("JOB-DOC/%s", meta.getJobInfo().getDoc());
                    byte[] doc = Common.readClassPathResource(resPath, loader);
                    meta.setDoc(doc);
                }
                if (StrUtil.isNotEmpty(meta.getJobInfo().getIcon())) {
                    String resPath = String.format("JOB-ICON/%s", meta.getJobInfo().getIcon());
                    byte[] icon = Common.readClassPathResource(resPath, loader);
                    meta.setIcon(icon);
                }
            }
        } catch (Exception e) {
            throw new JaxException("scan error", e);
        }
        return result;
    }

    private List<String> genArgs(String mode, String path, String output) {
        List<String> args = new ArrayList<>();
        args.add("--action");
        args.add("scan");
        args.add("--mode");
        args.add(mode);
        args.add("--path");
        args.add(path);
        args.add("--output");
        args.add(output);
        return args;
    }

    private void safeDelete(String path) {
        try {
            FileUtil.del(path);
        } catch (Exception ignore) {
        }
    }
}
