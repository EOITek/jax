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

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public static void addDir(ZipOutputStream zip, String path) throws IOException {
        path = StrUtil.addSuffixIfNot(path, StrUtil.SLASH);
        zip.putNextEntry(new ZipEntry(path));
        zip.closeEntry();
    }

    public static void addFile(ZipOutputStream zip, String name, String path) throws IOException {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            addFile(zip, name, new FileInputStream(file));
        }
    }

    public static void addFile(ZipOutputStream zip, String name, byte[] bytes) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(bytes);
        zip.closeEntry();
    }

    public static void addFile(ZipOutputStream zip, String name, InputStream in) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        IoUtil.copy(in, zip);
        zip.closeEntry();
        in.close();
    }
}
