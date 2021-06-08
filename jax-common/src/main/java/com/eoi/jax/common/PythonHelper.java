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

package com.eoi.jax.common;

import cn.hutool.core.io.IoUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PythonHelper {

    public static final String CONF_FILE = "jax_conf.json";
    public static final String PYTHON = "python";
    public static final String PYTHON_VIRTUAL_PACKAGE_SPARK = "python.spark.";
    public static final String PYTHON_VIRTUAL_PACKAGE_FLINK = "python.flink.";

    private PythonHelper() {
    }

    public static boolean isPython(String className) {
        return className.startsWith(PYTHON_VIRTUAL_PACKAGE_SPARK) || className.startsWith(PYTHON_VIRTUAL_PACKAGE_FLINK);
    }

    /**
     * extract python module from class loader
     * 只是一个非常tricky的实现
     * 由于flink不支持自定义上传资源文件，仅支持设置class path。所以在提交任务的时候，将python包打包到一个临时的jar包中
     * 提交任务的时候使用-C指定这个jar
     * 于是，在tm上可以通过ClassLoader来加载python包
     * jar包中规定下面的目录结构
     * xxx.jar
     *    | -- _jax_artifacts
     *                | -- manifest
     *                | -- jax_python.zip
     *                | -- &lt;user_code1.zip&gt;
     *                | -- &lt;user_code2.zip&gt;
     *                | -- ...
     * _jax_artifacts/manifest 清单文件，其中包含如下信息
     *      jax_python.zip
     *      &lt;user_code1.zip&gt;
     *      &lt;user_code2.zip&gt;
     *
     *  首先读取manifest文件，将其中的所有zip文件读取，并保存在当前路径，返回manifest中的项
     */
    public static List<String> extractPythonModules(ClassLoader callerClassLoader) throws Exception {
        InputStream manifest = callerClassLoader.getResourceAsStream("_jax_artifacts/manifest");
        if (manifest == null) {
            return Collections.emptyList();
        }
        List<String> artifacts = new ArrayList<>();
        IoUtil.readLines(manifest, StandardCharsets.UTF_8, artifacts);
        for (String artifact : artifacts) {
            String zipFileName = "_jax_artifacts/" + artifact;
            InputStream zipStream = callerClassLoader.getResourceAsStream(zipFileName);
            if (zipStream == null) {
                // remove from artifacts?
                continue;
            }
            File zipFile = new File(artifact); // 当前目录
            OutputStream zipFileStream = new FileOutputStream(zipFile);
            IoUtil.copy(zipStream, zipFileStream);
        }
        return artifacts;
    }
}
