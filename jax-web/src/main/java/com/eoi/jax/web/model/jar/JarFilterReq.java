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
import com.eoi.jax.web.model.BaseFilter;

public class JarFilterReq implements BaseFilter<TbJar> {
    private String search;
    private String jobType;
    private String jarVersion;
    private String clusterName;

    public String getSearch() {
        return search;
    }

    public JarFilterReq setSearch(String search) {
        this.search = search;
        return this;
    }

    public String getJobType() {
        return jobType;
    }

    public JarFilterReq setJobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    public String getJarVersion() {
        return jarVersion;
    }

    public JarFilterReq setJarVersion(String jarVersion) {
        this.jarVersion = jarVersion;
        return this;
    }

    public String getClusterName() {
        return clusterName;
    }

    public JarFilterReq setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    @Override
    public QueryWrapper<TbJar> where(QueryWrapper<TbJar> wrapper) {
        wrapper.lambda()
                .and(StrUtil.isNotBlank(search), q -> q
                        .like(TbJar::getJarFile, search)
                        .or()
                        .like(TbJar::getJarDescription, search))
                .eq(StrUtil.isNotBlank(jobType), TbJar::getJobType, jobType)
                .eq(StrUtil.isNotBlank(jarVersion), TbJar::getJarVersion, jarVersion)
                .eq(StrUtil.isNotBlank(clusterName), TbJar::getClusterName, clusterName);
        return wrapper;
    }
}
