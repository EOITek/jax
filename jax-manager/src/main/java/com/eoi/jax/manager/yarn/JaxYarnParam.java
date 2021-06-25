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

package com.eoi.jax.manager.yarn;

public class JaxYarnParam {
    private Integer timeOutMs;
    private String principal;
    private String keytab;
    private String applicationId;
    private String hadoopConfDir;

    public Integer getTimeOutMs() {
        return timeOutMs;
    }

    public JaxYarnParam setTimeOutMs(Integer timeOutMs) {
        this.timeOutMs = timeOutMs;
        return this;
    }

    public String getPrincipal() {
        return principal;
    }

    public JaxYarnParam setPrincipal(String principal) {
        this.principal = principal;
        return this;
    }

    public String getKeytab() {
        return keytab;
    }

    public JaxYarnParam setKeytab(String keytab) {
        this.keytab = keytab;
        return this;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public JaxYarnParam setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getHadoopConfDir() {
        return hadoopConfDir;
    }

    public JaxYarnParam setHadoopConfDir(String hadoopConfDir) {
        this.hadoopConfDir = hadoopConfDir;
        return this;
    }
}
