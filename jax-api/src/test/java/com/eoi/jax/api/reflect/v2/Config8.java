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

import java.util.List;

public class Config8 {

    @Parameter(optional = true)
    private List<Config7> p15;

    @Parameter(optional = true)
    private List<List<Config7>> p16;

    public List<Config7> getP15() {
        return p15;
    }

    public Config8 setP15(List<Config7> p15) {
        this.p15 = p15;
        return this;
    }

    public List<List<Config7>> getP16() {
        return p16;
    }

    public Config8 setP16(List<List<Config7>> p16) {
        this.p16 = p16;
        return this;
    }
}
