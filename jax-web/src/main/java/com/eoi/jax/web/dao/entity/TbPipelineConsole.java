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

@TableName("tb_pipeline_console")
public class TbPipelineConsole {

    @TableField("pipeline_name")
    private String pipelineName;

    @TableField("op_type")
    private String opType;

    @TableField("op_time")
    private Long opTime;

    @TableField("log_content")
    private String logContent;

    @TableField("create_time")
    private Long createTime;

    public String getPipelineName() {
        return pipelineName;
    }

    public TbPipelineConsole setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
        return this;
    }

    public String getOpType() {
        return opType;
    }

    public TbPipelineConsole setOpType(String opType) {
        this.opType = opType;
        return this;
    }

    public Long getOpTime() {
        return opTime;
    }

    public TbPipelineConsole setOpTime(Long opTime) {
        this.opTime = opTime;
        return this;
    }

    public String getLogContent() {
        return logContent;
    }

    public TbPipelineConsole setLogContent(String logContent) {
        this.logContent = logContent;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public TbPipelineConsole setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }
}