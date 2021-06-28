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

package com.eoi.jax.web.debug;

public enum MsgCode {
    START_DEBUG("1001"),
    SOURCE_DATA("1002"),
    SINK_DATA("1003"),
    DEBUG_LOG("1004"),
    WEB_URL("1005"),
    ERROR("9999");

    public final String code;

    MsgCode(String code) {
        this.code = code;
    }

    public boolean isEqual(String code) {
        return this.code.equals(code);
    }
}
