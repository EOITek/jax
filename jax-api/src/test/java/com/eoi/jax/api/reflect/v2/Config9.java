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

public class Config9 {

    @Parameter(optional = true)
    private List<Config8> p17;

    @Parameter(optional = true)
    private List<List<Config8>> p18;

    public List<Config8> getP17() {
        return p17;
    }

    public Config9 setP17(List<Config8> p17) {
        this.p17 = p17;
        return this;
    }

    public List<List<Config8>> getP18() {
        return p18;
    }

    public Config9 setP18(List<List<Config8>> p18) {
        this.p18 = p18;
        return this;
    }
}
