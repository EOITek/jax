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

import com.eoi.jax.web.common.util.Common;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jax")
public class JaxConfig implements ConfigToMap {
    private static final String JAX_PYTHON_NAME = "jax_python.zip";

    private Integer serverId;
    private String mode;
    private String home;
    private String work;
    private String jarTmp;
    private String jarDir;
    private String jarLib;
    private String jarHdfsDir;
    private Boolean corsEnabled;
    private BuildInConfig buildin;
    private WebsiteConfig website;
    private ThreadPoolConfig thread;
    private DebugConfig debug;
    private ToolConfig tool;

    public Integer getServerId() {
        return serverId;
    }

    public JaxConfig setServerId(Integer serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getMode() {
        return mode;
    }

    public JaxConfig setMode(String mode) {
        this.mode = mode;
        return this;
    }

    public String getHome() {
        return home;
    }

    public JaxConfig setHome(String home) {
        this.home = home;
        return this;
    }

    public String getWork() {
        return work;
    }

    public JaxConfig setWork(String work) {
        this.work = work;
        return this;
    }

    public String getJarTmp() {
        return jarTmp;
    }

    public JaxConfig setJarTmp(String jarTmp) {
        this.jarTmp = jarTmp;
        return this;
    }

    public String getJarDir() {
        return jarDir;
    }

    public JaxConfig setJarDir(String jarDir) {
        this.jarDir = jarDir;
        return this;
    }

    public String getJarLib() {
        return jarLib;
    }

    public JaxConfig setJarLib(String jarLib) {
        this.jarLib = jarLib;
        return this;
    }

    public String getJarHdfsDir() {
        return jarHdfsDir;
    }

    public JaxConfig setJarHdfsDir(String jarHdfsDir) {
        this.jarHdfsDir = jarHdfsDir;
        return this;
    }

    public Boolean getCorsEnabled() {
        return corsEnabled;
    }

    public JaxConfig setCorsEnabled(Boolean corsEnabled) {
        this.corsEnabled = corsEnabled;
        return this;
    }

    public BuildInConfig getBuildin() {
        return buildin;
    }

    public JaxConfig setBuildin(BuildInConfig buildin) {
        this.buildin = buildin;
        return this;
    }

    public WebsiteConfig getWebsite() {
        return website;
    }

    public JaxConfig setWebsite(WebsiteConfig website) {
        this.website = website;
        return this;
    }

    public ThreadPoolConfig getThread() {
        return thread;
    }

    public JaxConfig setThread(ThreadPoolConfig thread) {
        this.thread = thread;
        return this;
    }

    public DebugConfig getDebug() {
        return debug == null ? new DebugConfig() : debug;
    }

    public JaxConfig setDebug(DebugConfig debug) {
        this.debug = debug;
        return this;
    }

    public ToolConfig getTool() {
        return tool;
    }

    public JaxConfig setTool(ToolConfig tool) {
        this.tool = tool;
        return this;
    }

    public String getJaxPythonName() {
        return JAX_PYTHON_NAME;
    }

    public String getJaxPythonDir() {
        return Common.pathsJoin(getHome(),"python");
    }

    public String getJaxPythonPath() {
        return Common.pathsJoin(getHome(), "python", JAX_PYTHON_NAME);
    }
}
