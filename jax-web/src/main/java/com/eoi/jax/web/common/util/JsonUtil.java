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

package com.eoi.jax.web.common.util;

import com.eoi.jax.web.common.exception.JaxException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    private JsonUtil() {
        // forbid init instance
    }

    public static String encodePretty(Object value)  {
        if (value == null) {
            return null;
        }
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (IOException e) {
            throw new JaxException(e);
        }
    }

    public static String encode(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(value);
        } catch (IOException e) {
            throw new JaxException(e);
        }
    }

    public static <T> T decode(String value, Class<T> valueType) {
        if (value == null) {
            return null;
        }
        try {
            return MAPPER.readValue(value, valueType);
        } catch (IOException e) {
            throw new JaxException(e);
        }
    }

    public static <T> T decode(byte[] value, Class<T> valueType) {
        if (value == null) {
            return null;
        }
        try {
            return MAPPER.readValue(value, valueType);
        } catch (IOException e) {
            throw new JaxException(e);
        }
    }

    public static <T> T decode(String value, TypeReference<T> valueTypeRef) {
        if (value == null) {
            return null;
        }
        try {
            return MAPPER.readValue(value, valueTypeRef);
        } catch (IOException e) {
            throw new JaxException(e);
        }
    }

    public static Map<String, Object> decode2Map(String value) {
        if (value == null) {
            return null;
        }
        return decode(value, new TypeReference<Map<String, Object>>() {});
    }

    public static List<Map<String, Object>> decode2ListMap(String value) {
        if (value == null) {
            return null;
        }
        return decode(value, new TypeReference<List<Map<String, Object>>>() {});
    }

    public static List<String> decode2ListString(String value) {
        if (value == null) {
            return null;
        }
        return decode(value, new TypeReference<List<String>>() {});
    }

    public static JsonNode decode2Tree(String value) {
        try {
            return MAPPER.readTree(value);
        } catch (IOException e) {
            throw new JaxException(e);
        }
    }
}
