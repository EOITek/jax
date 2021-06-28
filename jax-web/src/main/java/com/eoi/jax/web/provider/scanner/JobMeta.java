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

package com.eoi.jax.web.provider.scanner;

import com.eoi.jax.web.common.consts.DataType;
import com.eoi.jax.web.common.consts.InputType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * copy from com.eoi.jax.tool.JobMeta
 */
public class JobMeta {
    private String jobName;
    private List<TypeDescriptor> inTypes;
    private List<TypeDescriptor> outTypes;
    private Experimantal experimantal;
    private JobInfo jobInfo;
    private JobParameters parameters;
    @JsonIgnore private byte[] doc;//doc的二进制内容，不参与序列化
    @JsonIgnore private byte[] icon;//icon的二进制内容，不参与序列化

    public String getJobName() {
        return jobName;
    }

    public JobMeta setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public List<TypeDescriptor> getInTypes() {
        return inTypes;
    }

    public JobMeta setInTypes(List<TypeDescriptor> inTypes) {
        this.inTypes = inTypes;
        return this;
    }

    public List<TypeDescriptor> getOutTypes() {
        return outTypes;
    }

    public JobMeta setOutTypes(List<TypeDescriptor> outTypes) {
        this.outTypes = outTypes;
        return this;
    }

    public Experimantal getExperimantal() {
        return experimantal;
    }

    public JobMeta setExperimantal(Experimantal experimantal) {
        this.experimantal = experimantal;
        return this;
    }

    public JobInfo getJobInfo() {
        return jobInfo;
    }

    public JobMeta setJobInfo(JobInfo jobInfo) {
        this.jobInfo = jobInfo;
        return this;
    }

    public JobParameters getParameters() {
        return parameters;
    }

    public JobMeta setParameters(JobParameters parameters) {
        this.parameters = parameters;
        return this;
    }

    public byte[] getDoc() {
        return doc;
    }

    public JobMeta setDoc(byte[] doc) {
        this.doc = doc;
        return this;
    }

    public byte[] getIcon() {
        return icon;
    }

    public JobMeta setIcon(byte[] icon) {
        this.icon = icon;
        return this;
    }

    public static class Experimantal {
        private String message;

        public String getMessage() {
            return message;
        }

        public Experimantal setMessage(String message) {
            this.message = message;
            return this;
        }
    }

    public static class TypeDescriptor {
        private String raw;
        private List<String> parameterizedTypes;

        public String getRaw() {
            return raw;
        }

        public TypeDescriptor setRaw(String raw) {
            this.raw = raw;
            return this;
        }

        public List<String> getParameterizedTypes() {
            return parameterizedTypes;
        }

        public TypeDescriptor setParameterizedTypes(List<String> parameterizedTypes) {
            this.parameterizedTypes = parameterizedTypes;
            return this;
        }
    }

    public static class JobInfo {
        private int apiVersion;
        private String name;
        private String type;
        private String role;
        private String category;
        private String display;
        private String description;
        private String doc;
        private String icon;
        private Boolean internal;

        public int getApiVersion() {
            return apiVersion;
        }

        public JobInfo setApiVersion(int apiVersion) {
            this.apiVersion = apiVersion;
            return this;
        }

        public String getName() {
            return name;
        }

        public JobInfo setName(String name) {
            this.name = name;
            return this;
        }

        public String getType() {
            return type;
        }

        public JobInfo setType(String type) {
            this.type = type;
            return this;
        }

        public String getRole() {
            return role;
        }

        public JobInfo setRole(String role) {
            this.role = role;
            return this;
        }

        public String getCategory() {
            return category;
        }

        public JobInfo setCategory(String category) {
            this.category = category;
            return this;
        }

        public String getDisplay() {
            return display;
        }

        public JobInfo setDisplay(String display) {
            this.display = display;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public JobInfo setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getDoc() {
            return doc;
        }

        public JobInfo setDoc(String doc) {
            this.doc = doc;
            return this;
        }

        public String getIcon() {
            return icon;
        }

        public JobInfo setIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public Boolean getInternal() {
            return internal;
        }

        public JobInfo setInternal(Boolean internal) {
            this.internal = internal;
            return this;
        }
    }

    public static class JobParameters {
        private List<JobParameter> parameters;

        public List<JobParameter> getParameters() {
            return parameters;
        }

        public JobParameters setParameters(List<JobParameter> parameters) {
            this.parameters = parameters;
            return this;
        }
    }

    public static class JobParameter {
        private int apiVersion;
        private String name;
        private String label;
        private DataType[] type;
        private String description;
        private boolean optional;
        private String defaultValue;
        private String placeholder;
        private String[] candidates;
        private InputType inputType;
        private String range;
        private String regex;
        private int order;
        private String requireCondition;
        private String availableCondition;
        private JobParameter listParameter;
        private List<JobParameter> objectParameters;

        public String getRequireCondition() {
            return requireCondition;
        }

        public void setRequireCondition(String requireCondition) {
            this.requireCondition = requireCondition;
        }

        public String getAvailableCondition() {
            return availableCondition;
        }

        public void setAvailableCondition(String availableCondition) {
            this.availableCondition = availableCondition;
        }

        public int getApiVersion() {
            return apiVersion;
        }

        public JobParameter setApiVersion(int apiVersion) {
            this.apiVersion = apiVersion;
            return this;
        }

        public String getName() {
            return name;
        }

        public JobParameter setName(String name) {
            this.name = name;
            return this;
        }

        public String getLabel() {
            return label;
        }

        public JobParameter setLabel(String label) {
            this.label = label;
            return this;
        }

        public DataType[] getType() {
            return type;
        }

        public JobParameter setType(DataType[] type) {
            this.type = type;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public JobParameter setDescription(String description) {
            this.description = description;
            return this;
        }

        public boolean isOptional() {
            return optional;
        }

        public JobParameter setOptional(boolean optional) {
            this.optional = optional;
            return this;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public JobParameter setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public String getPlaceholder() {
            return placeholder;
        }

        public JobParameter setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public String[] getCandidates() {
            return candidates;
        }

        public JobParameter setCandidates(String[] candidates) {
            this.candidates = candidates;
            return this;
        }

        public InputType getInputType() {
            return inputType;
        }

        public JobParameter setInputType(InputType inputType) {
            this.inputType = inputType;
            return this;
        }

        public String getRange() {
            return range;
        }

        public JobParameter setRange(String range) {
            this.range = range;
            return this;
        }

        public String getRegex() {
            return regex;
        }

        public JobParameter setRegex(String regex) {
            this.regex = regex;
            return this;
        }

        public int getOrder() {
            return order;
        }

        public JobParameter setOrder(int order) {
            this.order = order;
            return this;
        }

        public JobParameter getListParameter() {
            return listParameter;
        }

        public JobParameter setListParameter(JobParameter listParameter) {
            this.listParameter = listParameter;
            return this;
        }

        public List<JobParameter> getObjectParameters() {
            return objectParameters;
        }

        public JobParameter setObjectParameters(List<JobParameter> objectParameters) {
            this.objectParameters = objectParameters;
            return this;
        }
    }
}
