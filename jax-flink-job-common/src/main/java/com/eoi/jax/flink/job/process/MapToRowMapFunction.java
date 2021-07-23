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

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.types.Row;

import java.util.Map;

public class MapToRowMapFunction implements MapFunction<Map<String, Object>, Row>, ResultTypeQueryable<Row> {

    private String[] fieldNames;
    private TypeInformation<Row> types;

    public MapToRowMapFunction(String[] fieldNames,
                               TypeInformation<Row> types) {
        this.fieldNames = fieldNames;
        this.types = types;
    }

    @Override
    public Row map(Map<String, Object> value) {
        Row r = new Row(fieldNames.length);
        for (int i = 0; i < fieldNames.length; i++) {
            r.setField(i, value.get(fieldNames[i]));
        }
        return r;
    }

    @Override
    public TypeInformation<Row> getProducedType() {
        return types;
    }
}
