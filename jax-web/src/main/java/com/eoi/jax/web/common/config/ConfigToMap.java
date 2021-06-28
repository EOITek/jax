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

package com.eoi.jax.web.common.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Map;

public interface ConfigToMap {
    default Map<String, Object> toMap() {
        Map<String, Object> map = BeanUtil.beanToMap(this);
        Field[] fields = ReflectUtil.getFields(this.getClass());
        for (Field field : fields) {
            String name = field.getName();
            try {
                field.setAccessible(true);
                Object obj = field.get(this);
                if (obj instanceof ConfigToMap) {
                    Map<String, Object> value = ((ConfigToMap) obj).toMap();
                    map.put(name, value);
                }
            } catch (Exception ignore) {
            }
        }
        return map;
    }
}
