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
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("tb_pipeline_log")
public class TbPipelineLog {
    @TableField("pipeline_name")
    private String pipelineName;

    @TableField("log_type")
    private String logType;

    @TableField("pipeline_log")
    private String pipelineLog;

    @TableField("create_time")
    private Long createTime;

    public String getPipelineName() {
        return pipelineName;
    }

    public TbPipelineLog setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
        return this;
    }

    public String getLogType() {
        return logType;
    }

    public TbPipelineLog setLogType(String logType) {
        this.logType = logType;
        return this;
    }

    public String getPipelineLog() {
        return pipelineLog;
    }

    public TbPipelineLog setPipelineLog(String pipelineLog) {
        this.pipelineLog = pipelineLog;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public TbPipelineLog setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }
}
