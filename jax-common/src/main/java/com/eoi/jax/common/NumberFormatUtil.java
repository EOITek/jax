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

import java.text.NumberFormat;

public class NumberFormatUtil {

    public static NumberFormat prettyDoubleFormat() {
        //double 数值过大或者小数位过多会变成科学计数法形式，需要做格式转换
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(false);        //禁用千分位分隔符
        numberFormat.setMaximumFractionDigits(10);  //小数部分最多保留10位
        return numberFormat;
    }
}
