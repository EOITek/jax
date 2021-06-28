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
import com.eoi.jax.web.common.config.ConfigLoader;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.dao.entity.TbJar;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class JarFileCache {

    public String genMarkFilePath(TbJar jar) {
        return Common.pathsJoin(
                ConfigLoader.load().jax.getJarDir(),
                genMarkFileName(jar)
        );
    }

    public String genMarkFileName(TbJar jar) {
        String fileExt = Common.getFileExtension(jar.getJarPath());
        return Common.genJarCache(jar.getJarName(), fileExt);
    }

    public String genMarkUUID(TbJar jar) {
        String string = JsonUtil.encode(jar);
        return DigestUtils.md5DigestAsHex(string.getBytes(StandardCharsets.UTF_8));
    }

    public JarCache getJarCache(TbJar jar) {
        String path = genMarkFilePath(jar);
        if (!FileUtil.exist(path)) {
            return null;
        }
        String content = FileUtil.readString(path, StandardCharsets.UTF_8);
        JarCache cache = JsonUtil.decode(content, JarCache.class);
        String jarPath = cache.getPath();
        String uuid = genMarkUUID(jar);
        if (StrUtil.equals(uuid, cache.getUuid())
                && FileUtil.exist(jarPath)) {
            return cache;
        }
        return null;
    }

    public void createJarCache(TbJar jar, String jarPath) {
        String markPath = genMarkFilePath(jar);
        String uuid = genMarkUUID(jar);
        JarCache cache = new JarCache();
        cache.setUuid(uuid);
        cache.setTime(jar.getUpdateTime());
        cache.setPath(jarPath);
        String content = JsonUtil.encode(cache);
        FileUtil.writeString(content, markPath, StandardCharsets.UTF_8);
    }

    public void deleteJarCache(TbJar jar) {
        String markPath = genMarkFilePath(jar);
        JarCache cache = getJarCache(jar);
        if (cache != null) {
            FileUtil.del(cache.getPath());
        }
        FileUtil.del(markPath);
    }
}
