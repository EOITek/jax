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

public class Config2 {

    @Parameter(defaultValue = "3")
    private Long p3;

    @Parameter(defaultValue = "4.4")
    private Float p4;

    @Shift
    private Config4 config4;

    @Parameter(optional = true)
    private Config6 config6;

    public Long getP3() {
        return p3;
    }

    public Config2 setP3(Long p3) {
        this.p3 = p3;
        return this;
    }

    public Float getP4() {
        return p4;
    }

    public Config2 setP4(Float p4) {
        this.p4 = p4;
        return this;
    }

    public Config4 getConfig4() {
        return config4;
    }

    public Config2 setConfig4(Config4 config4) {
        this.config4 = config4;
        return this;
    }

    public Config6 getConfig6() {
        return config6;
    }

    public Config2 setConfig6(Config6 config6) {
        this.config6 = config6;
        return this;
    }
}
