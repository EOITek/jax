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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;


public class VersionConfig {
    private static final Logger logger = LoggerFactory.getLogger(VersionConfig.class);

    private static final Properties props = new Properties();

    static {
        try {
            props.load(VersionConfig.class.getResourceAsStream("/jax.git.properties"));
        } catch (Exception e) {
            logger.error("load config error", e);
        }
    }

    private VersionConfig() {
        // forbid init instance
    }

    public static Properties getProperties() {
        return props;
    }

    public static Map<String, String> getMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (final String name : props.stringPropertyNames()) {
            map.put(name, props.get(name).toString());
        }
        return map;
    }


}
