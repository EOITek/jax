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

public class Config5 {
    @Parameter(defaultValue = "p9")
    private String p9;

    @Parameter(defaultValue = "10")
    private Integer p10;

    @Shift
    private Config8 config8;

    @Parameter(optional = true)
    private Config9 config9;

    public String getP9() {
        return p9;
    }

    public Config5 setP9(String p9) {
        this.p9 = p9;
        return this;
    }

    public Integer getP10() {
        return p10;
    }

    public Config5 setP10(Integer p10) {
        this.p10 = p10;
        return this;
    }

    public Config8 getConfig8() {
        return config8;
    }

    public Config5 setConfig8(Config8 config8) {
        this.config8 = config8;
        return this;
    }

    public Config9 getConfig9() {
        return config9;
    }

    public Config5 setConfig9(Config9 config9) {
        this.config9 = config9;
        return this;
    }
}
