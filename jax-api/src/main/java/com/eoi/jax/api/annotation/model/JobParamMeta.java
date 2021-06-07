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

package com.eoi.jax.api.annotation.model;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.api.annotation.InputType;
import com.eoi.jax.api.annotation.Parameter;

import java.lang.reflect.Field;

public class JobParamMeta extends ParamMeta implements Comparable<JobParamMeta> {

    private int apiVersion;
    private boolean algorithmic;
    private String placeholder;
    private String[] candidates;
    private InputType inputType;
    private String range;
    private String regex;
    // v2 支持
    private int order;
    // v2 支持
    private JobParamMeta listParameter;
    // v2 支持
    private JobParamMeta[] objectParameters;
    // v2 支持
    private String requireCondition;
    // v2 支持
    private String availableCondition;

    public JobParamMeta() {
    }

    public JobParamMeta(Parameter annotation) {
        this.setApiVersion(2);
        this.setName(annotation.name());
        this.setLabel(annotation.label());
        this.setDescription(annotation.description());
        this.setOptional(annotation.optional());
        this.setDefaultValue(annotation.defaultValue());
        this.setPlaceholder(annotation.placeholder());
        this.setCandidates(annotation.candidates());
        this.setInputType(annotation.inputType());
        this.setRange(annotation.range());
        this.setRegex(annotation.regex());
        this.setOrder(annotation.order());
        this.setAvailableCondition(annotation.availableCondition());
        this.setRequireCondition(annotation.requireCondition());
    }


    public JobParamMeta setParentAvailableCondition(String parentAvailableCondition) {
        String avCondition;
        if (StrUtil.isNotBlank(parentAvailableCondition) && StrUtil.isNotBlank(getAvailableCondition())) {
            avCondition = String.format("%s && (%s)", parentAvailableCondition, getAvailableCondition());
        } else if (StrUtil.isNotBlank(parentAvailableCondition)) {
            avCondition = parentAvailableCondition;
        } else {
            avCondition = getAvailableCondition();
        }
        this.setAvailableCondition(avCondition);
        return this;
    }

    @Override
    public JobParamMeta setField(Field field) {
        super.setField(field);
        return this;
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public ParamMeta setApiVersion(int apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    public boolean getAlgorithmic() {
        return algorithmic;
    }

    public JobParamMeta setAlgorithmic(boolean algorithmic) {
        this.algorithmic = algorithmic;
        return this;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public JobParamMeta setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public String[] getCandidates() {
        return candidates;
    }

    public JobParamMeta setCandidates(String[] candidates) {
        this.candidates = candidates;
        return this;
    }

    public InputType getInputType() {
        return inputType;
    }

    public JobParamMeta setInputType(InputType inputType) {
        this.inputType = inputType;
        return this;
    }

    public String getRange() {
        return range;
    }

    public JobParamMeta setRange(String range) {
        this.range = range;
        return this;
    }

    public String getRegex() {
        return regex;
    }

    public JobParamMeta setRegex(String regex) {
        this.regex = regex;
        return this;
    }

    public int getOrder() {
        return order;
    }

    public JobParamMeta setOrder(int order) {
        this.order = order;
        return this;
    }

    public JobParamMeta getListParameter() {
        return listParameter;
    }

    public JobParamMeta setListParameter(JobParamMeta listParameter) {
        this.listParameter = listParameter;
        return this;
    }

    public JobParamMeta[] getObjectParameters() {
        return objectParameters;
    }

    public JobParamMeta setObjectParameters(JobParamMeta[] objectParameters) {
        this.objectParameters = objectParameters;
        return this;
    }

    public String getRequireCondition() {
        return requireCondition;
    }

    public JobParamMeta setRequireCondition(String requireCondition) {
        this.requireCondition = requireCondition;
        return this;
    }

    public String getAvailableCondition() {
        return availableCondition;
    }

    public JobParamMeta setAvailableCondition(String availableCondition) {
        this.availableCondition = availableCondition;
        return this;
    }

    @Override
    public int compareTo(JobParamMeta o) {
        return this.getOrder() - o.getOrder();
    }
}
