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

public class Config3 {

    @Parameter(defaultValue = "5.5")
    private Double p5;

    @Parameter(defaultValue = "true")
    private Boolean p6;

    @Shift
    private Config6 config6;

    @Parameter(optional = true)
    private Config7 config7;

    public Double getP5() {
        return p5;
    }

    public Config3 setP5(Double p5) {
        this.p5 = p5;
        return this;
    }

    public Boolean getP6() {
        return p6;
    }

    public Config3 setP6(Boolean p6) {
        this.p6 = p6;
        return this;
    }

    public Config6 getConfig6() {
        return config6;
    }

    public Config3 setConfig6(Config6 config6) {
        this.config6 = config6;
        return this;
    }

    public Config7 getConfig7() {
        return config7;
    }

    public Config3 setConfig7(Config7 config7) {
        this.config7 = config7;
        return this;
    }
}
