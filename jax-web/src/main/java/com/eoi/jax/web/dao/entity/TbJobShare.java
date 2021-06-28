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

@TableName
public class TbJobShare {

    @TableId("share_name")
    private String shareName;

    @TableField("job_name")
    private String jobName;

    @TableField("job_config")
    private String jobConfig;

    @TableField("create_time")
    private Long createTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_time")
    private Long updateTime;

    @TableField("update_by")
    private String updateBy;

    public String getShareName() {
        return shareName;
    }

    public TbJobShare setShareName(String shareName) {
        this.shareName = shareName;
        return this;
    }

    public String getJobName() {
        return jobName;
    }

    public TbJobShare setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public String getJobConfig() {
        return jobConfig;
    }

    public TbJobShare setJobConfig(String jobConfig) {
        this.jobConfig = jobConfig;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public TbJobShare setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public TbJobShare setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public TbJobShare setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public TbJobShare setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return this;
    }
}