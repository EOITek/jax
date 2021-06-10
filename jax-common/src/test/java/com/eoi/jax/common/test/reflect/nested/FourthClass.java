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

package com.eoi.jax.common.test.reflect.nested;

public class FourthClass implements ThirdInterface<T2> {

    private Long p3;

    private Double p4;

    public Long getP3() {
        return p3;
    }

    public FourthClass setP3(Long p3) {
        this.p3 = p3;
        return this;
    }

    public Double getP4() {
        return p4;
    }

    public FourthClass setP4(Double p4) {
        this.p4 = p4;
        return this;
    }
}
