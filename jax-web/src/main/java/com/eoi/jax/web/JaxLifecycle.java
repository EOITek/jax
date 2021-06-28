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

package com.eoi.jax.web;

import com.eoi.jax.web.schedule.JaxScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Profile("!test")
@Component
public class JaxLifecycle implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(JaxLifecycle.class);
    private ApplicationContext applicationContext;

    @Autowired
    private JaxScheduler jaxScheduler;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applicationReady() {
        jaxScheduler.initialize();
        JaxApp.println("Jax Application is Ready");
    }

    @PreDestroy
    public void destroy() {
        logger.info("Jax Application is Destroying");
        jaxScheduler.destroy();
    }
}
