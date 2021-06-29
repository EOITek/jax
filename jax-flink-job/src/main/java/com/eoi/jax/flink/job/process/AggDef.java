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

import com.eoi.jax.api.annotation.InputType;
import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;
import java.util.List;


public class AggDef implements Serializable {
    public static final String AGG_METHOD_MAX = "max";
    public static final String AGG_METHOD_MIN = "min";
    public static final String AGG_METHOD_AVG = "avg";
    public static final String AGG_METHOD_LAST = "last";
    public static final String AGG_METHOD_FIRST = "first";
    public static final String AGG_METHOD_SUM = "sum";
    public static final String AGG_METHOD_COUNT = "count";
    public static final String AGG_METHOD_COLLECT = "collect";
    public static final String AGG_METHOD_CALC = "calc";

    @Parameter(
            label = "聚合字段",
            description = "计算`sum`,`max`,`min`,`avg`方法聚合字段为数值类型；"
                    + "其他方法可支持其他类型；",
            optional = true
    )
    private String fieldName;

    @Parameter(
            label = "聚合字段表达式",
            description = "使用表达式计算得出参与聚合字段的值",
            optional = true,
            inputType = InputType.AVIATOR_SCRIPT
    )
    private String fieldExp;

    @Parameter(
            label = "聚合方法",
            description = "支持 `max`,`min`,`avg`,`last`,`first`,`sum`,`count`,`collect`,`calc`;"
                    + "count当字段名为* 则无条件count，否则需要对应字段的值不为null才count；"
                    + "collect收集指定字段为LIST；"
                    + "calc按aviator表达式通过其他聚合结果变量计算新的结果；",
            candidates = {AGG_METHOD_MAX, AGG_METHOD_MIN, AGG_METHOD_AVG, AGG_METHOD_LAST, AGG_METHOD_FIRST, AGG_METHOD_SUM, AGG_METHOD_COUNT, AGG_METHOD_COLLECT, AGG_METHOD_CALC}
    )
    private String aggMethod;

    @Parameter(
            label = "aviator表达式",
            description = "当aggMethod为calc时，通过其他方法的输出字段 或 窗口时间字段 或 分组字段 为变量来指定aviator表达式，计算聚合结果；"
                    + "变量如果包含特殊字符串，需要以 # 符号开始,再使用两个 ` 符号来包围例如： #`@timestamp`；",
            optional = true,
            inputType = InputType.AVIATOR_SCRIPT
    )
    private String calc;

    @Parameter(
            label = "收集字段白名单",
            description = "当aggMethod为collect时，需要指定收集的字段名列表",
            optional = true
    )
    private List<String> collectFieldNames;

    @Parameter(
            label = "收集条数限制",
            description = "当aggMethod为collect时，收集的条数限制",
            optional = true
    )
    private Integer limitCount;

    @Parameter(
            label = "收集策略",
            description = "定义如何在窗口内收集数据，与limitCount一起使用，取前多少条（top），取后多少条（bottom），随机取多少条（random）",
            defaultValue = "top",
            candidates = {"top","bottom","random"}
    )
    private String collectType;

    @Parameter(
            label = "是否去重",
            description = "对于窗口内所有输入数据聚合字段的值，是否去重后聚合",
            defaultValue = "false"
    )
    private boolean distinct;

    @Parameter(
            label = "输出字段",
            description = "不可重复且命名以字母或下划线开头；"
    )
    private String outputFieldName;

    @Parameter(
            label = "过滤表达式",
            description = "表达式aviator格式，只有符合才参与变量聚合计算",
            optional = true,
            inputType = InputType.AVIATOR_SCRIPT
    )
    private String filterExp;

    @Parameter(
            label = "数值精度",
            description = "聚合后输出平均值的数值精度，如果不填，默认保持原始精度",
            optional = true
    )
    private Integer scale;

    public static AggDef of(String fieldName,String aggMethod,String outputFieldName) {
        return AggDef.of(fieldName, aggMethod, outputFieldName,null,null,null,null,false,null,null);
    }

    public static AggDef of(String fieldName,String aggMethod,String outputFieldName,
                            String calc, List<String> collectFieldNames, Integer limitCount,String collectType,
                            boolean distinct,String filterExp, Integer scale) {
        AggDef aggDef = new AggDef();
        aggDef.setAggMethod(aggMethod);
        aggDef.setFieldName(fieldName);
        aggDef.setOutputFieldName(outputFieldName);
        aggDef.setScale(scale);
        aggDef.setCalc(calc);
        aggDef.setCollectFieldNames(collectFieldNames);
        aggDef.setLimitCount(limitCount);
        aggDef.setCollectType(collectType);
        aggDef.setDistinct(distinct);
        aggDef.setFilterExp(filterExp);
        return aggDef;
    }

    public String getFieldExp() {
        return fieldExp;
    }

    public void setFieldExp(String fieldExp) {
        this.fieldExp = fieldExp;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getAggMethod() {
        return aggMethod;
    }

    public void setAggMethod(String aggMethod) {
        this.aggMethod = aggMethod;
    }

    public String getOutputFieldName() {
        return outputFieldName;
    }

    public void setOutputFieldName(String outputFieldName) {
        this.outputFieldName = outputFieldName;
    }

    public String getCalc() {
        return calc;
    }

    public void setCalc(String calc) {
        this.calc = calc;
    }

    public List<String> getCollectFieldNames() {
        return collectFieldNames;
    }

    public void setCollectFieldNames(List<String> collectFieldNames) {
        this.collectFieldNames = collectFieldNames;
    }

    public Integer getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }

    public String getCollectType() {
        return collectType;
    }

    public void setCollectType(String collectType) {
        this.collectType = collectType;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public String getFilterExp() {
        return filterExp;
    }

    public void setFilterExp(String filterExp) {
        this.filterExp = filterExp;
    }
}
