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

package com.eoi.jax.web.common.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class Common {
    private Common() {
        // forbid init instance
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static boolean verifyName(String name) {
        if (StrUtil.isBlank(name)) {
            return false;
        }
        int length = name.length();
        if (length > 255) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            char v = name.charAt(i);
            if (!Character.isDigit(v)
                    && !Character.isLowerCase(v)
                    && !Character.isUpperCase(v)
                    && v != '-'
                    && v != '_') {
                return false;
            }
        }
        return true;
    }

    public static String pathsJoin(String first, String... more) {
        Path path = Paths.get(first, more);
        return path.toString();
    }

    public static String urlPathsJoin(String first, String... more) {
        for (String item : more) {
            if (first.endsWith("/") && item.startsWith("/")) {
                first = String.format("%s%s", first.substring(0, first.length() - 1), item);
            } else if (first.endsWith("/") || item.startsWith("/")) {
                first = String.format("%s%s", first, item);
            } else {
                first = String.format("%s/%s", first, item);
            }
        }
        return first;
    }

    public static String toStringNull(Number num) {
        return null == num ? null : String.valueOf(num);
    }

    public static String genJarName(String jarName, String fileExt) {
        return "jax-" + jarName + fileExt;
    }

    public static String genJarCache(String jarName, String fileExt) {
        return genJarName(jarName, fileExt) + ".cache";
    }

    public static String getFileExtension(String path) {
        if (".tar.gz".equals(path)) {
            return ".tar.gz";
        }
        return "." + FileUtil.extName(path);
    }

    public static List<String> listJars(String path) {
        List<String> result = new ArrayList<>();
        if (!FileUtil.exist(path) || !FileUtil.isDirectory(path)) {
            return result;
        }
        File[] files = FileUtil.ls(path);
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                result.add(file.getAbsolutePath());
            }
        }
        return result;
    }

    public static byte[] readClassPathResource(String path, ClassLoader loader) {
        ClassPathResource resource = new ClassPathResource(path, loader);
        return resource.readBytes();
    }
}
