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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WindowAggAccItem implements Serializable {

    // 统计值
    private Long count;
    private Double sum;
    private Double max;
    private Double min;
    private Object first;
    private Object last;
    private List<Map<String,Object>> collectList;
    private Integer limitCount;
    private String collectType;
    private Boolean distinct;

    private String aggMethod;
    private Integer scale;

    private Set<String> distinctSet;

    public Set<String> getDistinctSet() {
        return distinctSet;
    }

    public void setDistinctSet(Set<String> distinctSet) {
        this.distinctSet = distinctSet;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
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

    public List<Map<String, Object>> getCollectList() {
        return collectList;
    }

    public void setCollectList(List<Map<String, Object>> collectList) {
        this.collectList = collectList;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getAggMethod() {
        return aggMethod;
    }

    public void setAggMethod(String aggMethod) {
        this.aggMethod = aggMethod;
    }

    public Object getFirst() {
        return first;
    }

    public void setFirst(Object first) {
        this.first = first;
    }

    public Object getLast() {
        return last;
    }

    public void setLast(Object last) {
        this.last = last;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }
}
