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

package com.eoi.jax.common;

public class OperatorCategory {

    private OperatorCategory() {

    }

    public static final String DATA_TYPE_CONVERSION = "数据类型转换";

    public static final String TEXT_ANALYSIS = "文本解析";

    public static final String DATA_TRANSFORMATION = "数据流变换";

    public static final String FIELD_PROCESSING = "字段处理";

    public static final String WINDOW_OPERATION = "窗口操作";

    public static final String MACHINE_LEARNING = "机器学习";

    public static final String SCRIPT_PROCESSING = "脚本处理";

    public static final String SPECIAL_OPERATOR = "专用算子";
}
