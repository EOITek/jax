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

package com.eoi.jax.flink.job.common;

import com.eoi.jax.api.annotation.InputType;
import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.api.reflect.ParamUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ElasticSearchSourceJobConfig implements Serializable {

    @Parameter(
            label = "es地址列表",
            description = "输入至少一个或多个es节点地址，接收http://host:port数组"
    )
    private List<String> nodes;

    @Parameter(
            label = "目标index",
            description = "可以是单个index，或者包含`*`通配符。也可以用逗号分隔多个index"
    )
    private String index;

    @Parameter(
            label = "目标index的type",
            description = "只能指定一种type(文档类型)，elasticsearch将在版本8开始完全弃用type，8以前的版本还可以用",
            optional = true
    )
    private String type;

    @Parameter(
            label = "DSL查询",
            description = "DSL查询，json字符串",
            inputType = InputType.JSON,
            optional = true
    )
    private String queryDsl;

    @Parameter(
            label = "认证方式",
            description = "认证方式，支持basic认证和kerberos认证方式",
            optional = true,
            defaultValue = "none",
            candidates = {"none", "basic", "kerberos"}
    )
    private String authType;

    @Parameter(
            label = "http认证用户名",
            description = "http认证用户名",
            optional = true,
            availableCondition = "authType=='basic'"
    )
    private String httpAuthUser;

    @Parameter(
            label = "http认证密码",
            description = "http认证密码",
            inputType = InputType.PASSWORD,
            optional = true,
            availableCondition = "authType=='basic'"
    )
    private String httpAuthPass;

    @Parameter(
            label = "每批数据大小",
            description = "读取数据时每批的大小",
            optional = true,
            defaultValue = "5000"
    )
    private Integer scrollSize;

    @Parameter(
            label = "scroll时间",
            description = "scroll的过期时间",
            optional = true,
            defaultValue = "5m"
    )
    private String scrollTime;

    @Parameter(
            label = "keytab路径",
            description = "kerberos认证访问的es的keytab路径",
            optional = true,
            availableCondition = "authType=='kerberos'"
    )
    private String kerberosKeytab;

    @Parameter(
            label = "principal名称",
            description = "kerberos认证访问的es的principal名称，例如tss@HADOOP.COM",
            optional = true,
            availableCondition = "authType=='kerberos'"
    )
    private String kerberosPrincipal;

    @Parameter(
            label = "请求es超时时间",
            description = "请求es超时时间，默认为1分钟",
            optional = true,
            defaultValue = "60000"
    )
    private Integer requestTimeout;

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getKerberosKeytab() {
        return kerberosKeytab;
    }

    public void setKerberosKeytab(String kerberosKeytab) {
        this.kerberosKeytab = kerberosKeytab;
    }

    public String getKerberosPrincipal() {
        return kerberosPrincipal;
    }

    public void setKerberosPrincipal(String kerberosPrincipal) {
        this.kerberosPrincipal = kerberosPrincipal;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getQueryDsl() {
        return queryDsl;
    }

    public void setQueryDsl(String queryDsl) {
        this.queryDsl = queryDsl;
    }

    public String getHttpAuthUser() {
        return httpAuthUser;
    }

    public void setHttpAuthUser(String httpAuthUser) {
        this.httpAuthUser = httpAuthUser;
    }

    public String getHttpAuthPass() {
        return httpAuthPass;
    }

    public void setHttpAuthPass(String httpAuthPass) {
        this.httpAuthPass = httpAuthPass;
    }

    public Integer getScrollSize() {
        return scrollSize;
    }

    public void setScrollSize(Integer scrollSize) {
        this.scrollSize = scrollSize;
    }

    public String getScrollTime() {
        return scrollTime;
    }

    public void setScrollTime(String scrollTime) {
        this.scrollTime = scrollTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static ElasticSearchSourceJobConfig fromMap(Map<String, Object> map) {
        ElasticSearchSourceJobConfig elasticSearchSourceJobConfig = new ElasticSearchSourceJobConfig();
        ParamUtil.configJobParams(elasticSearchSourceJobConfig, map);
        return elasticSearchSourceJobConfig;
    }
}
