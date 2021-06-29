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

import java.io.Serializable;


public class ColumnOpDef implements Serializable {
    public static final String METHOD_ADD = "add";
    public static final String METHOD_RENAME = "rename";
    public static final String METHOD_REMOVE = "remove";

    private String inputFieldName;

    private String colMethod;

    private String outputFieldName;

    private boolean outputFieldNameIsExp;

    private String outputValueExp;

    private String fallback;

    private boolean isReplace;

    public boolean isReplace() {
        return isReplace;
    }

    public ColumnOpDef setReplace(boolean replace) {
        isReplace = replace;
        return this;
    }

    public String getFallback() {
        return fallback;
    }

    public ColumnOpDef setFallback(String fallback) {
        this.fallback = fallback;
        return this;
    }

    public String getInputFieldName() {
        return inputFieldName;
    }

    public ColumnOpDef setInputFieldName(String inputFieldName) {
        this.inputFieldName = inputFieldName;
        return this;
    }

    public String getColMethod() {
        return colMethod;
    }

    public ColumnOpDef setColMethod(String colMethod) {
        this.colMethod = colMethod;
        return this;
    }

    public String getOutputFieldName() {
        return outputFieldName;
    }

    public ColumnOpDef setOutputFieldName(String outputFieldName) {
        this.outputFieldName = outputFieldName;
        return this;
    }

    public boolean isOutputFieldNameIsExp() {
        return outputFieldNameIsExp;
    }

    public ColumnOpDef setOutputFieldNameIsExp(boolean outputFieldNameIsExp) {
        this.outputFieldNameIsExp = outputFieldNameIsExp;
        return this;
    }

    public String getOutputValueExp() {
        return outputValueExp;
    }

    public ColumnOpDef setOutputValueExp(String outputValueExp) {
        this.outputValueExp = outputValueExp;
        return this;
    }
}
