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

import cn.hutool.core.util.StrUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetAddress;

@Configuration
@ConfigurationProperties(prefix = "server")
public class ServerConfig implements ConfigToMap {

    private int port;

    private Advertised advertised;

    private Ssl ssl;

    public int getPort() {
        return port;
    }

    public ServerConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public Advertised getAdvertised() {
        return advertised;
    }

    public ServerConfig setAdvertised(Advertised advertised) {
        this.advertised = advertised;
        return this;
    }

    public Ssl getSsl() {
        return ssl;
    }

    public ServerConfig setSsl(Ssl ssl) {
        this.ssl = ssl;
        return this;
    }

    public boolean isSslEnabled() {
        return ssl != null && Boolean.TRUE.equals(ssl.getEnabled());
    }

    public String getListenAddress() throws IOException {
        if (StrUtil.isNotBlank(advertised.getAddress())) {
            return advertised.getAddress();
        }
        return InetAddress.getLocalHost().getHostAddress();
    }

    public int getListenPort() {
        if (advertised.getPort() != null) {
            return advertised.getPort();
        }
        return port;
    }

    public String getListenHttp() throws IOException {
        String schema = "http";
        if (isSslEnabled()) {
            schema = "https";
        }
        return String.format("%s://%s:%s", schema, getListenAddress(), getListenPort());
    }

    public String getListenWebsocket() throws IOException {
        String schema = "ws";
        if (isSslEnabled()) {
            schema = "wss";
        }
        return String.format("%s://%s:%s", schema, getListenAddress(), getListenPort());
    }

    public static class Advertised implements ConfigToMap {

        private String address;

        private Integer port;

        public String getAddress() {
            return address;
        }

        public Advertised setAddress(String address) {
            this.address = address;
            return this;
        }

        public Integer getPort() {
            return port;
        }

        public Advertised setPort(Integer port) {
            this.port = port;
            return this;
        }
    }

    public static class Ssl {
        private Boolean enabled;

        public Boolean getEnabled() {
            return enabled;
        }

        public Ssl setEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }
    }
}
