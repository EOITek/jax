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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
@ConfigurationProperties(prefix = "jax.websocket")
public class WebSocketConfig {
    public static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;

    private Integer bufferSize = DEFAULT_BUFFER_SIZE;

    public Integer getBufferSize() {
        return bufferSize;
    }

    public WebSocketConfig setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    @Bean
    public ServerEndpointExporter serverEndpoint() {
        return new ServerEndpointExporter();
    }

    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean(
            @Value("${jax.websocket.bufferSize}") Integer bufferSize) {
        if (bufferSize == null || bufferSize <= 0) {
            bufferSize = DEFAULT_BUFFER_SIZE;
        }
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(bufferSize);
        container.setMaxBinaryMessageBufferSize(bufferSize);
        return container;
    }
}
