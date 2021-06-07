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

import com.eoi.jax.api.annotation.FlatStringMap;
import com.eoi.jax.api.annotation.Parameter;

import java.util.List;

public class Config4 {

    @Parameter(optional = true)
    private List<String> p7;

    @Parameter(optional = true)
    private FlatStringMap p8;

    public List<String> getP7() {
        return p7;
    }

    public Config4 setP7(List<String> p7) {
        this.p7 = p7;
        return this;
    }

    public FlatStringMap getP8() {
        return p8;
    }

    public Config4 setP8(FlatStringMap p8) {
        this.p8 = p8;
        return this;
    }
}
