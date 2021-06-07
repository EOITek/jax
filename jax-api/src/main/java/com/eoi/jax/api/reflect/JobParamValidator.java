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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.api.InvalidConfigParam;
import com.eoi.jax.api.annotation.DataType;
import com.eoi.jax.api.annotation.model.JobParamMeta;

import java.util.List;
import java.util.Optional;

public class JobParamValidator {

    public static void validate(JobParamMeta meta, Optional<Object> value) {
        if (!checkRequired(meta, value)) {
            throw new InvalidConfigParam(meta.getName(),
                    "required",
                    "the parameter " + meta.getName() + " is required");
        }
    }

    public static boolean checkRequired(JobParamMeta meta, Optional<Object> value) {
        if (meta.getOptional()) {
            return true;
        }
        if (!value.isPresent()) {
            return false;
        }

        Object object = value.get();

        if (DataType.LIST == meta.getType()[0] && object instanceof List) {
            return CollUtil.isNotEmpty((List) object);
        }

        if (DataType.STRING == meta.getType()[0] && object instanceof String) {
            return StrUtil.isNotEmpty((String) object);
        }
        return true;
    }
}
