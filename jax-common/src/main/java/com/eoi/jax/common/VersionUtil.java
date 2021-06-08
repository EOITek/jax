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

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

public class VersionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionUtil.class);
    public static final List<String> SUPPORT_OP = Arrays.asList("!=", ">=", "<=", "~>", ">", "<", "=", "*");

    public static String loadSupportVersion(String jarPath, ClassLoader classLoader) throws IOException {
        String supportVersion = "[[\"*\"]]";
        try (URLClassLoader loader = new URLClassLoader(
                new URL[] {new File(jarPath).toURI().toURL()},
                classLoader
        )) {
            try {
                ClassPathResource resource = new ClassPathResource("supportVersion", loader);
                supportVersion = resource.readUtf8Str();
            } catch (Exception ignore) {
            }
        }
        return supportVersion;
    }

    public static boolean validate(String versionExp, String ver) {
        if (StrUtil.isEmpty(ver) || StrUtil.isEmpty(versionExp)) {
            return false;
        }

        List<List<String>> versionList = parse(versionExp);
        if (versionList == null) {
            return false;
        }
        for (List<String> verList : versionList) {
            boolean allMatch = true;
            for (String v : verList) {
                if (!match(v, ver)) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) {
                return true;
            }
        }

        return false;
    }

    private static List<List<String>> parse(String versionExp) {
        try {
            return JsonUtil.decode(versionExp, new TypeReference<List<List<String>>>() {
            });
        } catch (IOException e) {
            LOGGER.error("parse version error:" + versionExp, e);
        }
        return null;
    }

    public static boolean match(String verExp, String ver) {
        if (StrUtil.isEmpty(verExp)) {
            return false;
        }
        if ("*".equals(verExp.trim())) {
            return true;
        }

        String verOp = "=";
        for (String op : SUPPORT_OP) {
            if (verExp.contains(op)) {
                verOp = op;
                break;
            }
        }

        String verNum = verExp.replaceAll(verOp, "").trim();
        if (StrUtil.isEmpty(verNum)) {
            return false;
        }

        int compare = StrUtil.compareVersion(ver, verNum);
        if ("~>".equals(verOp)) {
            return compare >= 0 && ver.startsWith(verNum);
        } else if ("=".equals(verOp)) {
            return compare == 0;
        } else if ("!=".equals(verOp)) {
            return compare != 0;
        } else if (">".equals(verOp)) {
            return compare > 0;
        } else if ("<".equals(verOp)) {
            return compare < 0;
        } else if (">=".equals(verOp)) {
            return compare >= 0;
        } else if ("<=".equals(verOp)) {
            return compare <= 0;
        } else {
            return false;
        }
    }
}
