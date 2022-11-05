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

package com.eoi.jax.web.model.opts;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.exception.BizException;
import com.eoi.jax.web.common.util.JsonUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public interface OptsResp {
    Long getUpdateTime();

    String getOptsType();

    String getOptsName();

    default List<OptsDescribe> getOptsList() {
        List<OptsDescribe> list = new ArrayList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                OptsValue annotation = field.getAnnotation(OptsValue.class);
                if (annotation == null) {
                    continue;
                }
                OptsDescribe describe = new OptsDescribe();
                describe.setName(field.getName());
                describe.setType(annotation.type().code);
                describe.setValue(field.get(this));
                describe.setDescription(annotation.description());
                describe.setLabel(annotation.label());
                describe.setRequired(annotation.required());
                list.add(describe);
            } catch (IllegalAccessException e) {
                throw new BizException(ResponseCode.INVALID_PARAM);
            }
        }
        return list;
    }

    default List<String> decode2ListString(String str) {
        if (StrUtil.isEmpty(str)) {
            return new ArrayList<>();
        }
        return JsonUtil.decode2ListString(str);
    }
}
