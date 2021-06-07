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

public class Config1 extends Config2 {

    @Parameter(defaultValue = "1")
    private String p1;

    @Parameter(defaultValue = "2")
    private Integer p2;

    @Shift
    private Config3 config3;

    @Parameter(optional = true)
    private Config5 config5;

    public String getP1() {
        return p1;
    }

    public Config1 setP1(String p1) {
        this.p1 = p1;
        return this;
    }

    public Integer getP2() {
        return p2;
    }

    public Config1 setP2(Integer p2) {
        this.p2 = p2;
        return this;
    }

    public Config3 getConfig3() {
        return config3;
    }

    public Config1 setConfig3(Config3 config3) {
        this.config3 = config3;
        return this;
    }

    public Config5 getConfig5() {
        return config5;
    }

    public Config1 setConfig5(Config5 config5) {
        this.config5 = config5;
        return this;
    }
}
