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

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.flink.job.common.AviatorUtil;
import org.apache.flink.api.common.functions.RichMapFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnOpFunction extends RichMapFunction<Map<String, Object>, Map<String, Object>> {

    private List<ColumnOpDef> opList;

    public ColumnOpFunction(List<ColumnOpDef> opList) {
        this.opList = opList;
    }

    @Override
    public Map<String, Object> map(Map<String, Object> value) throws Exception {
        Map<String, Object> result = new HashMap<>(value);

        for (ColumnOpDef columnOpDef : this.opList) {
            if (ColumnOpDef.METHOD_ADD.equals(columnOpDef.getColMethod())) {
                String outputFn = getOutputFieldName(columnOpDef, result);
                if (!StrUtil.isEmpty(outputFn)) {
                    boolean willAddOrReplace = !result.containsKey(outputFn)
                            || (result.containsKey(outputFn) && columnOpDef.isReplace());
                    if (willAddOrReplace) {
                        Object val = AviatorUtil.eval(columnOpDef.getOutputValueExp(), result, columnOpDef.getFallback());
                        result.put(outputFn, val);
                    }
                }
            } else if (ColumnOpDef.METHOD_RENAME.equals(columnOpDef.getColMethod())) {
                if (result.containsKey(columnOpDef.getInputFieldName())) {
                    Object val = result.get(columnOpDef.getInputFieldName());
                    String output = columnOpDef.getOutputFieldName();
                    if (!StrUtil.isEmpty(output)) {
                        boolean willAddOrReplace = !result.containsKey(output)
                                || (result.containsKey(output) && columnOpDef.isReplace());
                        if (willAddOrReplace) {
                            result.put(output, val);
                            result.remove(columnOpDef.getInputFieldName());
                        }
                    }
                }
            } else if (ColumnOpDef.METHOD_REMOVE.equals(columnOpDef.getColMethod())) {
                if (result.containsKey(columnOpDef.getInputFieldName())) {
                    result.remove(columnOpDef.getInputFieldName());
                }
            }
        }

        return result;
    }

    private String getOutputFieldName(ColumnOpDef columnOpDef, Map<String, Object> value) {
        if (columnOpDef.isOutputFieldNameIsExp()) {
            Object val = AviatorUtil.eval(columnOpDef.getOutputFieldName(), value, null);
            if (val != null) {
                return val.toString();
            } else {
                return null;
            }
        } else {
            return columnOpDef.getOutputFieldName();
        }
    }
}
