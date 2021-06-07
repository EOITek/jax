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

import com.eoi.jax.api.annotation.DataType;
import com.eoi.jax.api.annotation.FlatStringMap;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParamTypeAnalyzer {

    public List<DataType> analyze(Field field) {
        List<DataType> list = new ArrayList<>();
        Type type = field.getType();
        DataType dataType = analyze(type);
        if (dataType != null) {
            list.add(dataType);
        }
        return list;
    }

    public DataType analyze(Type type) {
        if (type == FlatStringMap.class) {
            return DataType.MAP;
        } else if (type == String.class) {
            return DataType.STRING;
        } else if (type == Integer.class) {
            return DataType.INT;
        } else if (type == int.class) {
            return DataType.INT;
        } else if (type == Long.class) {
            return DataType.LONG;
        } else if (type == long.class) {
            return DataType.LONG;
        } else if (type == Float.class) {
            return DataType.FLOAT;
        } else if (type == float.class) {
            return DataType.FLOAT;
        } else if (type == Double.class) {
            return DataType.DOUBLE;
        } else if (type == double.class) {
            return DataType.DOUBLE;
        } else if (type == Boolean.class) {
            return DataType.BOOL;
        } else if (type == boolean.class) {
            return DataType.BOOL;
        } else if (type == List.class) {
            return DataType.LIST;
        } else if (type == Map.class) {
            return DataType.MAP;
        }
        return DataType.OBJECT;
    }
}
