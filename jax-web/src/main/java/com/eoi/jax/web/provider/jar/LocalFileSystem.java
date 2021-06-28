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

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.common.exception.JaxException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class LocalFileSystem {

    public String saveTmpFile(MultipartFile file, String tmpPath) {
        try {
            FileUtil.del(tmpPath);
            File tmpFile = FileUtil.touch(tmpPath);
            file.transferTo(tmpFile);
        } catch (Exception e) {
            throw new JaxException(e);
        }
        return tmpPath;
    }

    public String move(String src, String dst) {
        if (StrUtil.equals(src, dst)) {
            return dst;
        }
        try {
            FileUtil.move(new File(src), new File(dst), true);
        } catch (Exception e) {
            throw new JaxException(e);
        }
        return dst;
    }

    public boolean delete(String path) {
        try {
            return FileUtil.del(path);
        } catch (Exception e) {
            throw new JaxException(e);
        }
    }
}
