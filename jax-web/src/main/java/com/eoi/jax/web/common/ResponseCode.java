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

package com.eoi.jax.web.common;

public enum ResponseCode {
    SUCCESS("0000", "成功"),
    FAILED("1000", "失败"),

    INVALID_PARAM("1001", "非法参数"),
    JSON_ERROR("1002", "转换json异常"),
    UPLOAD_ERROR("1003", "上传失败"),
    DOWNLOAD_ERROR("1004", "下载失败"),
    UPDATE_ERROR("1005", "更新失败"),


    JAR_EXIST("2001", "Jar包已存在，无法继续"),
    JAR_NOT_EXIST("2002", "Jar包不存在，无法继续"),
    JAR_INVALID_NAME("2002", "Jar包名称非法，无法继续"),
    PYTHON_NOT_EXIST("2003","Python包不存在,无法继续"),
    JAR_USED("2004", "删除的Jar正在使用中，无法继续"),

    MODULE_EXIST("2011","新增module已存在，无法继续"),
    MODULE_INVALID_NAME("2012", "module包名称非法，无法继续"),
    MODULE_NOT_EXIST("2013", "module包不存在，无法继续"),
    MODULE_CONFIG_NOT_EXIST("2014", "module包配置缺失，无法继续"),

    JOB_ERROR("2101", "Jar包解析失败，请重新上传"),
    JOB_EXIST("2103", "新增Job已存在，无法继续"),
    JOB_NOT_EXIST("2104", "Job不存在，无法继续"),
    JOB_USED("2105", "删除的Job正在使用中，无法继续"),
    JOB_EMPTY("2106", "无有效Job，请重新上传"),
    JOB_PARAM_NOT_EXIST("2121", "Job参数不存在，无法继续"),
    JOB_PARAM_EXIST("2122", "Job参数已存在，无法继续"),
    JOB_CONFIG_NOT_EXIST("2141", "Job参数配置不存在，无法继续"),
    JOB_CONFIG_EXIST("2142", "Job参数配置已存在，无法继续"),
    JOB_PARAM_ILLEGAL("2143","Job参数非法"),
    JOB_SHARE_NOT_EXIST("2151", "不存在，无法继续"),
    JOB_SHARE_EXIST("2152", "已存在，无法继续"),

    PIPELINE_EXIST("2201", "Pipeline已存在"),
    PIPELINE_NOT_EXIST("2202", "Pipeline不存在"),
    PIPELINE_INVALID_NAME("2203", "名称非法（只能使用字母数字-_）"),
    PIPELINE_CANNOT_STOP("2204", "不能停止Pipeline"),
    PIPELINE_CANNOT_START("2205", "不能启动Pipeline"),
    PIPELINE_CANNOT_DELETE("2206", "不能删除Pipeline"),
    PIPELINE_CANNOT_UPDATE("2205", "该状态下不能更新pipeline配置"),
    PIPELINE_CANNOT_DRAFT("2205", "不能保存草稿"),
    PIPELINE_INVALID_TYPE("2207", "Pipeline类型非法"),
    PIPELINE_INVALID_CONF("2208", "Pipeline配置非法"),
    PIPELINE_UPDATE_CHANGED("2209", "Pipeline已发生变化"),
    PIPELINE_INVALID_CLUSTER("2210", "Pipeline不能提交到该集群上"),
    PIPELINE_UNDEFINED_CLUSTER("2211", "未指定集群且未找到默认集群"),
    PIPELINE_INVALID_SCRIPT("2212", "非法的script"),
    PIPELINE_INVALID_VERSION("2213", "运行框架版本非法"),

    CLUSTER_EXIST("2301", "集群已存在"),
    CLUSTER_NOT_EXIST("2302", "集群不存在"),
    CLUSTER_OPTS_FLINK_REQUIRED("2303", "缺少Flink配置"),
    CLUSTER_OPTS_SPARK_REQUIRED("2304", "缺少Spark配置"),
    CLUSTER_OPTS_MARAYARN_REQUIRED("2312", "缺少marayarn配置"),
    CLUSTER_USED_PIPELINE("2305", "删除的集群正被Pipeline使用中，无法继续"),
    CLUSTER_USED_JAR("2306", "删除的集群包含上传的Jar，无法继续"),
    CLUSTER_DEFAULT_EXIST("2307", "已有默认集群"),
    CLUSTER_INVALID_DEFAULT_FLINK("2308", "不能作为默认Flink集群"),
    CLUSTER_INVALID_DEFAULT_SPARK("2309", "不能作为默认Spark集群"),
    CLUSTER_INVALID_TYPE("2310", "非法的集群类型"),
    CLUSTER_INVALID_NAME("2211", "名称非法（只能使用字母数字-_）"),
    OPTS_EXIST("2321", "配置已存在"),
    OPTS_NOT_EXIST("2322", "配置不存在"),
    OPTS_USED("2323", "删除的配置正在使用中，无法继续"),
    OPTS_INVALID_NAME("2211", "名称非法（只能使用字母数字-_）"),
    INVALID_HADOOP_CONF("2341", "读取Hadoop配置失败"),
    INVALID_UPLOAD_CLUSTER("2342", "不能上传到该集群"),

    DEBUG_FAILED("2501", "调试失败"),
    DEBUG_NOT_OPEN("2502", "调试未开启"),
    DEBUG_ALREADY_STARTED("2503", "调试模式已开启"),
    DEBUG_INVALID_MSG("2504", "非法消息"),
    DEBUG_NOT_SUPPORT("2505", "不支持调试"),


    DB_FAILED("9998", "数据库操作失败"),
    UNDEFINED("9999", "内部错误，请联系管理员");

    public final String code;
    public final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
