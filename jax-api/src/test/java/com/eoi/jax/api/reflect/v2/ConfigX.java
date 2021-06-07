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

public class ConfigX {

    @Parameter(
            label = "p1"
    )
    private String p1;

    @Parameter(
            label = "p2",
            availableCondition = "p1 == 'eoi'",
            requireCondition = "p1 == 'eoi' && p1 == 'tek'"
    )
    private String p2;

    @Shift(availableCondition = "p2!=''")
    private ConfigY configY;

    public ConfigY getConfigY() {
        return configY;
    }

    public void setConfigY(ConfigY configY) {
        this.configY = configY;
    }

    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getP2() {
        return p2;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }
}
