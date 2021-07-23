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

import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.FlatStringMap;
import com.eoi.jax.api.annotation.InputType;
import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.common.JsonUtil;
import org.apache.http.HttpHost;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElasticSearchSinkJobConfig implements ConfigValidatable, Serializable {

    public static final String DEFAULT_BULK_ACTIONS = "1500";
    public static final String DEFAULT_BULK_SIZE = "10";
    public static final String DEFAULT_FLUSH_INTERVAL = "1000";
    public static final String DEFAULT_FLUSH_BACKOFF_TYPE = "CONSTANT";
    public static final String DEFAULT_FLUSH_BACKOFF_RETRIES = "10000";
    public static final String DEFAULT_FLUSH_BACKOFF_DELAY = "60000";
    public static final String DEFAULT_REQUEST_TIMEOUT = "60000";
    public static final String DEFAULT_FLUSH_BACKOFF_ENABLE = "false";

    @Parameter(
            label = "es地址列表",
            description = "es节点列表，格式为host1:9200。支持前缀schema，如https://host3:port3。"
    )
    private List<String> esNodes;
    private transient List<HttpHost> esNodesHttpHost;

    @Parameter(
            label = "index pattern",
            description = "写入es的索引模式，支持按照时间以及提取数据内的字段作为索引名，例如 jax_%{+yyyy-MM-dd}_%{field}, 动态字段需要以 %{}包含，包含+的为日期，否则为字段提取"
    )
    private String indexPattern;

    @Parameter(
            label = "index type",
            description = "针对es7.x版本, 该字段必填, 而且只能为'_doc'; 针对es6.x以及以下版本, 该字段必填(不可为_doc), 用户可以自定义",
            defaultValue = "_doc"
    )
    private String indexType;

    @Parameter(
            label = "单批写入最大记录条数",
            description = "批量提交到es的记录条数条件，注意es的批处理条件包含：记录数，大小，以及时间间隔，三个条件只要有一个满足，就会提交到es。",
            defaultValue = DEFAULT_BULK_ACTIONS
    )
    private int bulkActions;

    @Parameter(
            label = "单批写入最大大小",
            description = "批量提交到es的记录大小条件(单位MB)，注意es的批处理条件包含：记录数，大小，以及时间间隔，三个条件只要有一个满足，就会提交到es。",
            defaultValue = DEFAULT_BULK_SIZE
    )
    private int bulkSize;

    @Parameter(
            label = "Flush的最大时间间隔(毫秒)",
            description = "批量提交到es的时间间隔条件(单位毫秒)，注意es的批处理条件包含：记录数，大小，以及时间间隔，三个条件只要有一个满足，就会提交到es。",
            defaultValue = DEFAULT_FLUSH_INTERVAL
    )
    private int flushInterval;

    @Parameter(
            label = "Doc ID的字段",
            description = "用来实现相同id的doc更新操作",
            optional = true
    )
    private String docIdField;

    @Parameter(
            label = "http header",
            description = "http header，如果es设定了安全策略，可以利用该参数设定对应的token。"
                    + "如果安全策略选择的是华为平台的kerberos，需要加上connection:close配置",
            optional = true
    )
    private FlatStringMap header;
    private String headerJson;

    @Parameter(
            label = "请求es超时时间（毫秒）",
            description = "请求es超时时间，默认为60000",
            defaultValue = DEFAULT_REQUEST_TIMEOUT
    )
    private int requestTimeout;

    @Parameter(
            label = "是否启用Flush backoff",
            description = "根据flink es connector的issue：https://issues.apache.org/jira/browse/FLINK-11046 ，"
                    + "可设置bulk.flush.backoff.enable: false，作为workaround",
            defaultValue = DEFAULT_FLUSH_BACKOFF_ENABLE
    )
    private boolean flushBackOffEnable;

    @Parameter(
            label = "Flush backoff类型",
            description = "间隔为固定时间（CONSTANT）间隔时间为指数级增长（EXPONENTIAL）",
            defaultValue = DEFAULT_FLUSH_BACKOFF_TYPE,
            candidates = {"CONSTANT","EXPONENTIAL"},
            optional = true,
            availableCondition = "flushBackOffEnable == true"
    )
    private String backoffType;

    @Parameter(
            label = "Flush backoff重试次数",
            description = "Flush backoff重试次数",
            defaultValue = DEFAULT_FLUSH_BACKOFF_RETRIES,
            optional = true,
            availableCondition = "flushBackOffEnable == true"
    )
    private int backoffRetries;

    @Parameter(
            label = "Flush backoff延迟",
            description = "Flush backoff延迟",
            defaultValue = DEFAULT_FLUSH_BACKOFF_DELAY,
            optional = true,
            availableCondition = "flushBackOffEnable == true"
    )
    private int backoffDelay;

    @Parameter(
            label = "是否开启es安全认证机制",
            description = "是否开启es安全认证机制？默认为false，如果设置为true，需要提供auth.user以及auth.password",
            defaultValue = "false"
    )
    private boolean authEnable;

    @Parameter(
            label = "访问es用户名",
            description = "访问es用户名",
            optional = true,
            availableCondition = "authEnable == true"
    )
    private String authUser;

    @Parameter(
            label = "访问es用户密码",
            description = "访问es用户密码",
            optional = true,
            inputType = InputType.PASSWORD,
            availableCondition = "authEnable == true"
    )
    private String authPassword;

    @Parameter(
            label = "es异常黑名单",
            description = "es写入失败的error message包含字符串，则直接报出异常，不继续任务",
            optional = true
    )
    private List<String> shouldThrowExMessages;

    @Parameter(
            label = "时间字段",
            description = "通过时间字段设置带日期的index名;如果index pattern里有日期变量则必填",
            optional = true
    )
    private String timeFieldName;

    @Parameter(
            label = "时间格式",
            description = "支持UNIX_S, UNIX_MS以及通用时间表达格式;如果指定时间字段则必填",
            defaultValue = "UNIX_MS"
    )
    private String timeFormat;

    @Parameter(
            label = "输入时间格式方言",
            description = "支持用户设定时间方言，默认的时区为: en-US，可选的时区参见列表: https://www.andiamo.co.uk/resources/iso-language-codes",
            optional = true,
            defaultValue = "en-US"
    )
    private String timeLocale;

    @Parameter(
            label = "keytab路径",
            description = "kerberos认证访问的es的keytab路径",
            optional = true
    )
    private String kerberosKeytab;

    @Parameter(
            label = "principal名称",
            description = "kerberos认证访问的es的principal名称，例如tss@HADOOP.COM",
            optional = true
    )
    private String kerberosPrincipal;

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

    public String getTimeFieldName() {
        return timeFieldName;
    }

    public void setTimeFieldName(String timeFieldName) {
        this.timeFieldName = timeFieldName;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getTimeLocale() {
        return timeLocale;
    }

    public void setTimeLocale(String timeLocale) {
        this.timeLocale = timeLocale;
    }

    public String getIndexPattern() {
        return indexPattern;
    }

    public void setIndexPattern(String indexPattern) {
        this.indexPattern = indexPattern;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public List<String> getEsNodes() {
        return esNodes;
    }

    public void setEsNodes(List<String> esNodes) {
        this.esNodes = esNodes;
    }

    public List<HttpHost> getEsNodesHttpHost() {
        return esNodesHttpHost;
    }

    public void setEsNodesHttpHost(List<HttpHost> esNodesHttpHost) {
        this.esNodesHttpHost = esNodesHttpHost;
    }

    public int getBulkActions() {
        return bulkActions;
    }

    public void setBulkActions(int bulkActions) {
        this.bulkActions = bulkActions;
    }

    public int getBulkSize() {
        return bulkSize;
    }

    public void setBulkSize(int bulkSize) {
        this.bulkSize = bulkSize;
    }

    public int getFlushInterval() {
        return flushInterval;
    }

    public void setFlushInterval(int flushInterval) {
        this.flushInterval = flushInterval;
    }

    public String getBackoffType() {
        return backoffType;
    }

    public void setBackoffType(String backoffType) {
        this.backoffType = backoffType;
    }

    public int getBackoffRetries() {
        return backoffRetries;
    }

    public void setBackoffRetries(int backoffRetries) {
        this.backoffRetries = backoffRetries;
    }

    public int getBackoffDelay() {
        return backoffDelay;
    }

    public void setBackoffDelay(int backoffDelay) {
        this.backoffDelay = backoffDelay;
    }

    public String getDocIdField() {
        return docIdField;
    }

    public void setDocIdField(String docIdField) {
        this.docIdField = docIdField;
    }

    public FlatStringMap getHeader() {
        return header;
    }

    public void setHeader(FlatStringMap header) {
        this.header = header;
    }

    public String getHeaderJson() {
        return headerJson;
    }

    public void setHeaderJson(String headerJson) {
        this.headerJson = headerJson;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public boolean isFlushBackOffEnable() {
        return flushBackOffEnable;
    }

    public void setFlushBackOffEnable(boolean flushBackOffEnable) {
        this.flushBackOffEnable = flushBackOffEnable;
    }

    public boolean isAuthEnable() {
        return authEnable;
    }

    public void setAuthEnable(boolean authEnable) {
        this.authEnable = authEnable;
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public List<String> getShouldThrowExMessages() {
        return shouldThrowExMessages;
    }

    public void setShouldThrowExMessages(List<String> shouldThrowExMessages) {
        this.shouldThrowExMessages = shouldThrowExMessages;
    }

    public ElasticSearchSinkJobConfig(Map<String, Object> map) {
        ParamUtil.configJobParams(this, map);

        esNodesHttpHost = new ArrayList<>();
        for (String nodeStr : this.esNodes) {
            HttpHost node = null;
            String schema = HttpHost.DEFAULT_SCHEME_NAME;
            if (nodeStr.startsWith("https://")) {
                schema = "https";
                nodeStr = nodeStr.replace("https://", "");
            } else if (nodeStr.startsWith("http://")) {
                nodeStr = nodeStr.replace("http://", "");
            }
            node = new HttpHost(nodeStr.split(":")[0], Integer.parseInt(nodeStr.split(":")[1]), schema);
            esNodesHttpHost.add(node);
        }

        if (this.header != null && this.header.size() > 0) {
            try {
                headerJson = JsonUtil.encode(this.header);
            } catch (Exception ignore) {

            }
        }
    }

    @Override
    public void validate() throws JobConfigValidationException {
        if (indexPattern.isEmpty()) {
            throw new JobConfigValidationException("index is null");
        }
        if (authEnable && (authUser.isEmpty() || authPassword.isEmpty())) {
            throw new JobConfigValidationException("auth.user and auth.password should be provided if es auth is enabled.");
        }
        if (bulkSize <= 0 || bulkActions <= 0 || flushInterval <= 0 || backoffRetries <= 0 || backoffDelay <= 0 || requestTimeout <= 0) {
            throw new JobConfigValidationException("bulkSize, bulkActions, flushInterval, backoffRetries, backoffDelay, requestTimeout should be > 0");
        }
    }

}
