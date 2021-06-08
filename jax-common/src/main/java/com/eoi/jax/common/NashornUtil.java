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

package com.eoi.jax.common;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NashornUtil {

    public static void accept(Object evalResult, Consumer<Map> consumer) {
        // evalResult 肯定实现了Map
        //  但可能是ScriptObjectMirror(js生成的对象)
        //    可能是array
        //  java原生的map
        if (evalResult == null) {
            return;
        }
        if (evalResult instanceof Map) {
            if (evalResult instanceof ScriptObjectMirror) {
                Object javaObject = somToJava(evalResult);
                if (javaObject instanceof Collection) {
                    for (Object item : (Collection) javaObject) {
                        consumer.accept((Map) item);
                    }
                } else {
                    consumer.accept((Map) javaObject);
                }
            } else {
                consumer.accept((Map) evalResult);
            }
        }
    }

    public static Object somToJava(Object object) {
        if (object instanceof ScriptObjectMirror) {
            ScriptObjectMirror som = (ScriptObjectMirror) object;
            if (som.isArray()) {
                List collection = new ArrayList();
                for (Object item : som.values()) {
                    collection.add(somToJava(item));
                }
                return collection;
            } else {
                Map<String, Object> map = new HashMap<>();
                for (Map.Entry<String, Object> entry : som.entrySet()) {
                    map.put(entry.getKey(), somToJava(entry.getValue()));
                }
                return map;
            }
        } else {
            return object;
        }
    }
}
