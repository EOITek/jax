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

import com.eoi.jax.web.dao.entity.TbJar;
import com.eoi.jax.web.dao.entity.TbJob;
import com.eoi.jax.web.model.BaseModel;
import com.eoi.jax.web.model.job.JobResp;

import java.util.List;
import java.util.stream.Collectors;

public class JarResp implements BaseModel<TbJar> {
    private String jarName;
    private String jarPath;
    private String jarDescription;
    private String jarVersion;
    private String supportVersion;
    private String jarFile;
    private String jarType;
    private String clusterName;
    private Long createTime;
    private String createBy;
    private Long updateTime;
    private String updateBy;
    private List<JobResp> jobList;

    public String getJarName() {
        return jarName;
    }

    public JarResp setJarName(String jarName) {
        this.jarName = jarName;
        return this;
    }

    public String getJarPath() {
        return jarPath;
    }

    public JarResp setJarPath(String jarPath) {
        this.jarPath = jarPath;
        return this;
    }

    public String getJarDescription() {
        return jarDescription;
    }

    public JarResp setJarDescription(String jarDescription) {
        this.jarDescription = jarDescription;
        return this;
    }

    public String getJarVersion() {
        return jarVersion;
    }

    public JarResp setJarVersion(String jarVersion) {
        this.jarVersion = jarVersion;
        return this;
    }

    public String getJarFile() {
        return jarFile;
    }

    public JarResp setJarFile(String jarFile) {
        this.jarFile = jarFile;
        return this;
    }

    public String getJarType() {
        return jarType;
    }

    public void setJarType(String jarType) {
        this.jarType = jarType;
    }

    public String getClusterName() {
        return clusterName;
    }

    public JarResp setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public JarResp setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public JarResp setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public JarResp setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public JarResp setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return this;
    }

    public List<JobResp> getJobList() {
        return jobList;
    }

    public JarResp setJobList(List<JobResp> jobList) {
        this.jobList = jobList;
        return this;
    }

    public String getSupportVersion() {
        return supportVersion;
    }

    public JarResp setSupportVersion(String supportVersion) {
        this.supportVersion = supportVersion;
        return this;
    }

    public JarResp respFrom(TbJar entity, List<TbJob> jobs) {
        JarResp resp = new JarResp().copyFrom(entity);
        resp.setJarType(entity.getJobType());
        resp.setJobList(jobs.stream().map(i -> new JobResp().respFrom(i, null)).collect(Collectors.toList()));
        return resp;
    }
}
