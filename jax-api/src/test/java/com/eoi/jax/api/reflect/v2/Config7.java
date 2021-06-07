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

public class Config7 {

    @Parameter(optional = true)
    private Double p14;

    @Parameter(defaultValue = "p15")
    private String p15;

    public Double getP14() {
        return p14;
    }

    public Config7 setP14(Double p14) {
        this.p14 = p14;
        return this;
    }

    public String getP15() {
        return p15;
    }

    public Config7 setP15(String p15) {
        this.p15 = p15;
        return this;
    }
}
