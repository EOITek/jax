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

package com.eoi.jax.flink.job.process;

import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;


public class RenameFieldsDef implements Serializable {

    @Parameter(
            label = "原始列",
            description = "重命名前列的名称"
    )
    private String oldField;

    @Parameter(
            label = "新列",
            description = "重命名后列的名称。如果该列名已存在，那么原来的列会被覆盖"
    )
    private String newField;

    @Parameter(
            label = "是否覆盖",
            description = "如果添加的字段已存在，是否覆盖原先的值",
            defaultValue = "false"
    )
    private boolean isReplace;

    public String getOldField() {
        return oldField;
    }

    public void setOldField(String oldField) {
        this.oldField = oldField;
    }

    public String getNewField() {
        return newField;
    }

    public void setNewField(String newField) {
        this.newField = newField;
    }

    public boolean isReplace() {
        return isReplace;
    }

    public void setReplace(boolean replace) {
        isReplace = replace;
    }
}
