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
import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class WindowAggJobConfig implements ConfigValidatable, Serializable {
    public static final String WINDOW_TYPE_TUMBLING = "tumbling";
    public static final String WINDOW_TYPE_SLIDING = "sliding";
    public static final String WINDOW_TYPE_SESSION = "session";
    public static final String WINDOW_TYPE_GLOBAL = "global";
    public static final String WINDOW_START = "start";
    public static final String WINDOW_END = "end";
    public static final String KEY_SPLITTER = "####";

    public static final String WINDOW_TRIGGER_DEFAULT = "default";
    public static final String WINDOW_TRIGGER_COUNT = "count";
    public static final String WINDOW_TRIGGER_DELTA = "delta";
    public static final String WINDOW_TRIGGER_EXPRESSION = "expression";

    public static final String WINDOW_TRIGGER_RESULT_CONTINUE = "CONTINUE";
    public static final String WINDOW_TRIGGER_RESULT_FIRE = "FIRE";
    public static final String WINDOW_TRIGGER_RESULT_PURGE = "PURGE";
    public static final String WINDOW_TRIGGER_RESULT_FIRE_AND_PURGE = "FIRE_AND_PURGE";

    @Parameter(
            label = "聚合窗口类型",
            description = "flink支持的窗口类型：https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/stream/operators/windows.html",
            candidates = {WINDOW_TYPE_TUMBLING, WINDOW_TYPE_SLIDING, WINDOW_TYPE_SESSION, WINDOW_TYPE_GLOBAL},
            defaultValue = WINDOW_TYPE_TUMBLING
    )
    private String windowType;

    @Parameter(
            label = "聚合窗口触发自定义类型",
            description = "默认按窗口类型的默认trigger触发，可选:default, count，delta，expression",
            candidates = {WINDOW_TRIGGER_DEFAULT, WINDOW_TRIGGER_COUNT, WINDOW_TRIGGER_DELTA, WINDOW_TRIGGER_EXPRESSION},
            defaultValue = WINDOW_TRIGGER_DEFAULT
    )
    private String triggerType;

    @Parameter(
            label = "聚合窗口触发动作",
            description = "窗口触发达到后所触发的动作；CONTINUE: do nothing,\n"
                    + "FIRE: trigger the computation,\n"
                    + "PURGE: clear the elements in the window, and\n"
                    + "FIRE_AND_PURGE: trigger the computation and clear the elements in the window afterwards",
            candidates = {WINDOW_TRIGGER_RESULT_CONTINUE, WINDOW_TRIGGER_RESULT_FIRE, WINDOW_TRIGGER_RESULT_PURGE, WINDOW_TRIGGER_RESULT_FIRE_AND_PURGE},
            defaultValue = WINDOW_TRIGGER_RESULT_FIRE
    )
    private String triggerResultType;

    @Parameter(
            label = "窗口触发最大count",
            description = "当窗口触发类型为count时必填，窗口内数据count数大于等于此最大值时窗口被触发",
            optional = true
    )
    private Long triggerMaxCount;

    @Parameter(
            label = "窗口触发delta比较值字段",
            description = "当窗口触发类型为delta时必填，比较值需为double类型；",
            optional = true
    )
    private String triggerDeltaValueField;

    @Parameter(
            label = "窗口触发delta阈值计算表达式",
            description = "当窗口触发类型为delta时必填，此表达式内可以使用变量为oldValue（上一条数据的delta比较值）和 newValue（本条数据的delta比较值），"
                    + "例如：newValue - oldValue*2.5 ，通过此表达式计算得出delta阈值",
            optional = true
    )
    private String triggerDeltaExp;

    @Parameter(
            label = "窗口触发delta的阈值",
            description = "当窗口触发类型为delta时必填，通过triggerDeltaExp计算值大于此阈值时窗口被触发",
            optional = true
    )
    private Double triggerDeltaThreshold;

    @Parameter(
            label = "窗口触发expression表达式",
            description = "当窗口触发类型为expression时必填，通过此表达式从map数据中计算得出boolean值，如果为true时窗口被触发",
            optional = true
    )
    private String triggerExpression;

    @Parameter(
            label = "聚合窗口时间(单位毫秒)",
            description = "聚合窗口时间(单位毫秒); 只有当窗口类型为global时是可选；",
            optional = true
    )
    private Long windowSize;

    @Parameter(
            label = "滑动窗口时间(单位毫秒)",
            description = "滑动窗口时间(单位毫秒)，windowType为sliding时需指定",
            optional = true
    )
    private Long slidingSize;

    @Parameter(
            label = "聚合定义",
            description = "聚合定义描述,包括聚合字段，聚合统计方法和输出字段"
    )
    private List<AggDef> aggDefs;

    @Parameter(
            label = "分组字段",
            description = "分组字段列表",
            optional = true
    )
    private List<String> groupKeys;

    @Parameter(
            label = "输出窗口时间字段",
            description = "窗口聚合后，按照窗口对应的时间，生成的时间戳字段名",
            optional = true,
            defaultValue = "@timestamp"
    )
    private String windowTimeColumn;

    @Parameter(
            label = "输出窗口时间格式",
            description = "转化后输出的目标格式，支持UNIX_S, UNIX_MS以及通用时间表达格式",
            optional = true,
            defaultValue = "UNIX_MS"
    )
    private String windowTimeFormat;

    @Parameter(
            label = "输出窗口时间的时区",
            description = "窗口时间所在时区, 例如：`Europe/London`,`PST` or `GMT+5`",
            optional = true
    )
    private String windowTimezone;

    @Parameter(
            label = "输出窗口时间类型",
            description = "窗口聚合后，按照窗口对应的时间，生成的时间戳时，是按照窗口的开始(start)还是结束(end)生成",
            candidates = {WINDOW_START, WINDOW_END},
            optional = true,
            defaultValue = WINDOW_START
    )
    private String windowTimeOffsetType;

    @Parameter(
            label = "输出时间格式方言",
            description = "支持用户设定时间方言，默认的时区为: en-US，可选的时区参见列表: https://www.andiamo.co.uk/resources/iso-language-codes",
            optional = true,
            defaultValue = "en-US"
    )
    private String windowTimeLocale;

    @Parameter(
            label = "最大可延迟时间",
            description = "event模式下，在延迟时间内，上一个窗口晚到的数据会使窗口重新触发聚合，请参考：https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/stream/operators/windows.html#allowed-lateness",
            optional = true,
            defaultValue = "0"
    )
    private int allowedLatenessTime;

    public int getAllowedLatenessTime() {
        return allowedLatenessTime;
    }

    public void setAllowedLatenessTime(int allowedLatenessTime) {
        this.allowedLatenessTime = allowedLatenessTime;
    }

    public String getTriggerResultType() {
        return triggerResultType;
    }

    public void setTriggerResultType(String triggerResultType) {
        this.triggerResultType = triggerResultType;
    }

    public String getTriggerDeltaValueField() {
        return triggerDeltaValueField;
    }

    public void setTriggerDeltaValueField(String triggerDeltaValueField) {
        this.triggerDeltaValueField = triggerDeltaValueField;
    }

    public String getWindowTimeLocale() {
        return windowTimeLocale;
    }

    public void setWindowTimeLocale(String windowTimeLocale) {
        this.windowTimeLocale = windowTimeLocale;
    }

    public String getWindowTimezone() {
        return windowTimezone;
    }

    public void setWindowTimezone(String windowTimezone) {
        this.windowTimezone = windowTimezone;
    }

    public String getWindowTimeFormat() {
        return windowTimeFormat;
    }

    public void setWindowTimeFormat(String windowTimeFormat) {
        this.windowTimeFormat = windowTimeFormat;
    }

    public String getWindowType() {
        return windowType;
    }

    public void setWindowType(String windowType) {
        this.windowType = windowType;
    }

    public Long getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(Long windowSize) {
        this.windowSize = windowSize;
    }

    public Long getSlidingSize() {
        return slidingSize;
    }

    public void setSlidingSize(Long slidingSize) {
        this.slidingSize = slidingSize;
    }

    public List<AggDef> getAggDefs() {
        return aggDefs;
    }

    public void setAggDefs(List<AggDef> aggDefs) {
        this.aggDefs = aggDefs;
    }

    public List<String> getGroupKeys() {
        return groupKeys;
    }

    public void setGroupKeys(List<String> groupKeys) {
        this.groupKeys = groupKeys;
    }

    public String getWindowTimeColumn() {
        return windowTimeColumn;
    }

    public void setWindowTimeColumn(String windowTimeColumn) {
        this.windowTimeColumn = windowTimeColumn;
    }

    public String getWindowTimeOffsetType() {
        return windowTimeOffsetType;
    }

    public void setWindowTimeOffsetType(String windowTimeOffsetType) {
        this.windowTimeOffsetType = windowTimeOffsetType;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public Long getTriggerMaxCount() {
        return triggerMaxCount;
    }

    public void setTriggerMaxCount(Long triggerMaxCount) {
        this.triggerMaxCount = triggerMaxCount;
    }

    public String getTriggerDeltaExp() {
        return triggerDeltaExp;
    }

    public void setTriggerDeltaExp(String triggerDeltaExp) {
        this.triggerDeltaExp = triggerDeltaExp;
    }

    public Double getTriggerDeltaThreshold() {
        return triggerDeltaThreshold;
    }

    public void setTriggerDeltaThreshold(Double triggerDeltaThreshold) {
        this.triggerDeltaThreshold = triggerDeltaThreshold;
    }

    public String getTriggerExpression() {
        return triggerExpression;
    }

    public void setTriggerExpression(String triggerExpression) {
        this.triggerExpression = triggerExpression;
    }

    @Override
    public void validate() throws JobConfigValidationException {
        if (WINDOW_TYPE_SLIDING.equals(windowType)) {
            if (slidingSize == null || slidingSize <= 0) {
                throw new JobConfigValidationException("missing slidingSize for sliding window");
            }
        }

        if (!WINDOW_TYPE_GLOBAL.equals(windowType)) {
            if (windowSize == null || windowSize <= 0) {
                throw new JobConfigValidationException("missing windowSize");
            }
        }

        if (WINDOW_TRIGGER_COUNT.equals(triggerType)) {
            if (triggerMaxCount == null) {
                throw new JobConfigValidationException("missing triggerMaxCount");
            }
        }

        if (WINDOW_TRIGGER_DELTA.equals(triggerType)) {
            if (triggerDeltaExp == null || triggerDeltaThreshold == null) {
                throw new JobConfigValidationException("missing triggerDeltaExp or triggerDeltaValue");
            }
        }

        if (WINDOW_TRIGGER_EXPRESSION.equals(triggerType)) {
            if (triggerExpression == null) {
                throw new JobConfigValidationException("missing triggerExpression");
            }
        }

        if (aggDefs != null) {
            Set<String> outputSet = new HashSet<>();
            for (AggDef aggDef : aggDefs) {
                if (AggDef.AGG_METHOD_CALC.equals(aggDef.getAggMethod())) {
                    if (StrUtil.isEmpty(aggDef.getCalc())) {
                        throw new JobConfigValidationException("missing calc exp for agg by calc method");
                    }
                }
                if (AggDef.AGG_METHOD_COLLECT.equals(aggDef.getAggMethod())) {
                    if (aggDef.getCollectFieldNames() == null || aggDef.getCollectFieldNames().isEmpty()) {
                        throw new JobConfigValidationException("missing CollectFieldNames for agg by collect method");
                    }
                }

                // AggDef 检查outputFieldName重复
                char firstChar = aggDef.getOutputFieldName().charAt(0);
                if (!Character.isLetter(firstChar) && firstChar != '_') {
                    throw new JobConfigValidationException(String.format("字段名需要以字母或下划线开头：%s", aggDef.getOutputFieldName()));
                }
                if (!outputSet.contains(aggDef.getOutputFieldName())) {
                    outputSet.add(aggDef.getOutputFieldName());
                } else {
                    throw new JobConfigValidationException(String.format("输出字段名重复：%s", aggDef.getOutputFieldName()));
                }
            }
        }
    }
}
