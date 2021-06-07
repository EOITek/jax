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

package com.eoi.jax.api.reflect.v2;

import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.api.annotation.Shift;

public class ConfigY {

    @Parameter(
            label = "p3"
    )
    private String p3;

    @Parameter(
            label = "p4",
            availableCondition = "p3 != 'ok'"
    )
    private String p4;

    @Shift(availableCondition = "p4!=''")
    private ConfigZ configZ;

    public ConfigZ getConfigZ() {
        return configZ;
    }

    public void setConfigZ(ConfigZ configZ) {
        this.configZ = configZ;
    }

    public String getP3() {
        return p3;
    }

    public void setP3(String p3) {
        this.p3 = p3;
    }

    public String getP4() {
        return p4;
    }

    public void setP4(String p4) {
        this.p4 = p4;
    }
}
