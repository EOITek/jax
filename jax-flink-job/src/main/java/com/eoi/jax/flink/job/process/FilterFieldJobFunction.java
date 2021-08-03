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

import cn.hutool.core.collection.CollUtil;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class FilterFieldJobFunction implements MapFunction<Map<String, Object>, Map<String, Object>> {

    private Set<String> whiteList;
    private Set<String> blackList;

    public FilterFieldJobFunction(List<String> whiteList, List<String> blackList) {
        this.whiteList = new HashSet<>(Optional.ofNullable(whiteList).orElse(Collections.emptyList()));
        this.blackList = new HashSet<>(Optional.ofNullable(blackList).orElse(Collections.emptyList()));
    }

    @Override
    public Map<String, Object> map(Map<String, Object> value) throws Exception {

        Map<String,Object> resultMap = new HashMap<>(value);
        if (CollUtil.isNotEmpty(whiteList)) {
            Map<String,Object> tmpMap = new HashMap<>();
            for (Map.Entry<String,Object> entry: resultMap.entrySet()) {
                if (whiteList.contains(entry.getKey())) {
                    tmpMap.put(entry.getKey(),entry.getValue());
                }
            }
            resultMap = tmpMap;
        }

        if (CollUtil.isNotEmpty(blackList)) {
            Map<String,Object> tmpMap = new HashMap<>();
            for (Map.Entry<String,Object> entry: resultMap.entrySet()) {
                if (!blackList.contains(entry.getKey())) {
                    tmpMap.put(entry.getKey(),entry.getValue());
                }
            }
            resultMap = tmpMap;
        }

        return resultMap;
    }
}
