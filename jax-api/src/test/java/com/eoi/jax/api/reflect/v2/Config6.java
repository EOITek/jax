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

public class Config6 {

    @Parameter(optional = true)
    private List<List<String>> p11;

    @Parameter(optional = true)
    private List<List<List<String>>> p12;

    public List<List<String>> getP11() {
        return p11;
    }

    public Config6 setP11(List<List<String>> p11) {
        this.p11 = p11;
        return this;
    }

    public List<List<List<String>>> getP12() {
        return p12;
    }

    public Config6 setP12(List<List<List<String>>> p12) {
        this.p12 = p12;
        return this;
    }
}
