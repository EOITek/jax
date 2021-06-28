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

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ConfigLoader implements ApplicationContextAware {
    private static ApplicationContext context;
    private static AppConfig config;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext; //NOSONAR
    }

    public static AppConfig load() {
        if (config == null) {
            config = context.getBean(AppConfig.class);
        }
        return config;
    }
}
