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

package com.eoi.jax.core;

import java.util.List;

public class PythonJobInfo {

    private String algClassName;
    private String algModuleName;
    private String display;
    private String description;
    private String type;

    private String algTrainType;
    private String algDetectType;
    private String algProcessType;
    private String algDataType;
    private String algTrainJob;
    private String algName;
    private String algAlias;
    private String algDescription;
    private String algVersion;
    private String algTrainDataLength;

    private List<PythonParameterInfo> parameters;

    public String getAlgTrainJob() {
        return algTrainJob;
    }

    public void setAlgTrainJob(String algTrainJob) {
        this.algTrainJob = algTrainJob;
    }

    public String getAlgDataType() {
        return algDataType;
    }

    public void setAlgDataType(String algDataType) {
        this.algDataType = algDataType;
    }

    public String getAlgProcessType() {
        return algProcessType;
    }

    public void setAlgProcessType(String algProcessType) {
        this.algProcessType = algProcessType;
    }

    public String getAlgTrainType() {
        return algTrainType;
    }

    public void setAlgTrainType(String algTrainType) {
        this.algTrainType = algTrainType;
    }

    public String getAlgDetectType() {
        return algDetectType;
    }

    public void setAlgDetectType(String algDetectType) {
        this.algDetectType = algDetectType;
    }

    public String getAlgName() {
        return algName;
    }

    public void setAlgName(String algName) {
        this.algName = algName;
    }

    public String getAlgAlias() {
        return algAlias;
    }

    public void setAlgAlias(String algAlias) {
        this.algAlias = algAlias;
    }

    public String getAlgDescription() {
        return algDescription;
    }

    public void setAlgDescription(String algDescription) {
        this.algDescription = algDescription;
    }

    public String getAlgVersion() {
        return algVersion;
    }

    public void setAlgVersion(String algVersion) {
        this.algVersion = algVersion;
    }

    public String getAlgTrainDataLength() {
        return algTrainDataLength;
    }

    public void setAlgTrainDataLength(String algTrainDataLength) {
        this.algTrainDataLength = algTrainDataLength;
    }

    public String getAlgClassName() {
        return algClassName;
    }

    public void setAlgClassName(String algClassName) {
        this.algClassName = algClassName;
    }

    public String getAlgModuleName() {
        return algModuleName;
    }

    public void setAlgModuleName(String algModuleName) {
        this.algModuleName = algModuleName;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PythonParameterInfo> getParameters() {
        return parameters;
    }

    public void setParameters(List<PythonParameterInfo> parameters) {
        this.parameters = parameters;
    }

    public static class PythonParameterInfo {

        private String name;

        private String label;

        private String description;

        private String type;

        private String optional;

        private String defaultValue;

        private String algParameterType;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOptional() {
            return optional;
        }

        public void setOptional(String optional) {
            this.optional = optional;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getAlgParameterType() {
            return algParameterType;
        }

        public PythonParameterInfo setAlgParameterType(String algParameterType) {
            this.algParameterType = algParameterType;
            return this;
        }
    }
}
