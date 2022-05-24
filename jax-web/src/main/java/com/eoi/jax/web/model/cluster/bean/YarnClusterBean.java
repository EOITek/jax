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

package com.eoi.jax.web.model.cluster.bean;

import com.eoi.jax.web.model.cluster.config.ConfigBean;
import com.eoi.jax.web.model.cluster.config.ConfigDef;

@ConfigBean("yarn")
public class YarnClusterBean extends CommClusterBean {

    @ConfigDef(label = "HADOOP_HOME",
            description = "配置JAX所在服务器的路径，可用${JAX_HOME}引用相对位置",
            required = false,
            displayPosition = 11)
    private String hadoopHome;

    @ConfigDef(label = "PYTHON_HOME",
            description = "配置Python环境",
            required = false,
            displayPosition = 12)
    private String pythonEnv;

    @ConfigDef(label = "Kerberos Principal",
            description = "如果hadoop集群开启认证，填写Kerberos认证对应的principal",
            required = false,
            displayPosition = 13)
    private String principal;

    @ConfigDef(label = "Kerberos Keytab",
            description = "如果hadoop集群开启认证，填写Kerberos认证对应的keytab文件。配置JAX所在服务器的路径，可用${JAX_HOME}引用相对位置",
            required = false,
            displayPosition = 14,type = ConfigDef.Type.TEXT)
    private String keytab;

    public String getHadoopHome() {
        return hadoopHome;
    }

    public void setHadoopHome(String hadoopHome) {
        this.hadoopHome = hadoopHome;
    }

    public String getPythonEnv() {
        return pythonEnv;
    }

    public void setPythonEnv(String pythonEnv) {
        this.pythonEnv = pythonEnv;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getKeytab() {
        return keytab;
    }

    public void setKeytab(String keytab) {
        this.keytab = keytab;
    }
}
