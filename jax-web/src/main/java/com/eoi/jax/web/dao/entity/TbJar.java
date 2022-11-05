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

package com.eoi.jax.web.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eoi.jax.web.common.util.Common;
import org.springframework.beans.BeanUtils;

@TableName("tb_jar")
public class TbJar {

    @TableId("jar_name")
    private String jarName;

    @TableField("jar_path")
    private String jarPath;

    @TableField("jar_description")
    private String jarDescription;

    @TableField("jar_version")
    private String jarVersion;

    @TableField("support_version")
    private String supportVersion;

    @TableField("jar_file")
    private String jarFile;

    @TableField("cluster_name")
    private String clusterName;

    @TableField("create_time")
    private Long createTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_time")
    private Long updateTime;

    @TableField("update_by")
    private String updateBy;

    @TableField("job_type")
    private String jobType;

    public String getJarName() {
        return jarName;
    }

    public TbJar setJarName(String jarName) {
        this.jarName = jarName;
        return this;
    }

    public String getJarPath() {
        return jarPath;
    }

    public TbJar setJarPath(String jarPath) {
        this.jarPath = jarPath;
        return this;
    }

    public String getJarDescription() {
        return jarDescription;
    }

    public TbJar setJarDescription(String jarDescription) {
        this.jarDescription = jarDescription;
        return this;
    }

    public String getJarVersion() {
        return jarVersion;
    }

    public TbJar setJarVersion(String jarVersion) {
        this.jarVersion = jarVersion;
        return this;
    }

    public String getJarFile() {
        return jarFile;
    }

    public TbJar setJarFile(String jarFile) {
        this.jarFile = jarFile;
        return this;
    }

    public String getClusterName() {
        return clusterName;
    }

    public TbJar setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public TbJar setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public TbJar setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public TbJar setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public TbJar setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return this;
    }

    public String getSupportVersion() {
        return supportVersion;
    }

    public TbJar setSupportVersion(String supportVersion) {
        this.supportVersion = supportVersion;
        return this;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public TbJar cloneFromDB() {
        TbJar clone = new TbJar();
        BeanUtils.copyProperties(this,clone);
        clone.setJarPath(Common.replaceJaxHomeAsPath(getJarPath()));
        return clone;
    }
}
