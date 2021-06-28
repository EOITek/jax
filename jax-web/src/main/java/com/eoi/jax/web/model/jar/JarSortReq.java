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

package com.eoi.jax.web.model.jar;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eoi.jax.web.dao.entity.TbJar;
import com.eoi.jax.web.model.BaseSort;

public class JarSortReq implements BaseSort<TbJar> {
    private String jarName;
    private String jarFile;
    private String createTime;
    private String updateTime;

    public String getJarName() {
        return jarName;
    }

    public JarSortReq setJarName(String jarName) {
        this.jarName = jarName;
        return this;
    }

    public String getJarFile() {
        return jarFile;
    }

    public JarSortReq setJarFile(String jarFile) {
        this.jarFile = jarFile;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public JarSortReq setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public JarSortReq setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public QueryWrapper<TbJar> order(QueryWrapper<TbJar> wrapper) {
        wrapper.lambda()
                .orderBy(StrUtil.isNotEmpty(jarName), isAsc(jarName), TbJar::getJarName)
                .orderBy(StrUtil.isNotEmpty(jarFile), isAsc(jarFile), TbJar::getJarFile)
                .orderBy(StrUtil.isNotEmpty(createTime), isAsc(createTime), TbJar::getCreateTime)
                .orderBy(StrUtil.isNotEmpty(updateTime), isAsc(updateTime), TbJar::getUpdateTime)
                .orderByDesc(StrUtil.isEmpty(updateTime), TbJar::getUpdateTime);
        return wrapper;
    }
}
