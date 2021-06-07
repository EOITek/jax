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

package com.eoi.jax.api.reflect;

import com.eoi.jax.api.annotation.model.JobParamMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParamUtil {

    public static List<JobParamMeta> scanJobParams(Class<?> configClass) {
        JobParamReflector reflector = new JobParamReflector();
        Map<String, JobParamMeta> map = reflector.scanParams(configClass);
        List<JobParamMeta> list = new ArrayList<>(map.values());
        return list.stream().sorted().collect(Collectors.toList());
    }

    public static Object configJobParams(Object config, Map<String, Object> map) {
        JobParamReflector reflector = new JobParamReflector();
        reflector.initParams(config);
        reflector.configParams(config, map);
        return config;
    }
}
