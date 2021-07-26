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

package com.eoi.jax.flink.job.sink;

import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.flink.job.process.SchemaColumnDef;
import org.apache.flink.table.descriptors.Schema;

import java.util.List;

public class JDBCSinkJobConfig extends JDBCSinkJobConfigBase {

    @Parameter(
            label = "输出表字段定义",
            description = "定义需要哪些字段输出到表"
    )
    private List<SchemaColumnDef> columns;

    public transient Schema schema;

    public List<SchemaColumnDef> getColumns() {
        return columns;
    }

    public void setColumns(List<SchemaColumnDef> columns) {
        this.columns = columns;
    }

    @Override
    public void validate() throws JobConfigValidationException {
    }
}
