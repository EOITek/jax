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

@ClassFlag
public class FourthClass3 extends FourthClass2 {

    //CHECKSTYLE.OFF:
    @Flag
    public FourthClass2 public_fourthClass2;

    @Flag
    private FourthClass private_fourthClass;
    //CHECKSTYLE.ON:

    private String p1;

    private Long p2;

    public String getP1() {
        return p1;
    }

    public FourthClass3 setP1(String p1) {
        this.p1 = p1;
        return this;
    }

    public Long getP2() {
        return p2;
    }

    public FourthClass3 setP2(Long p2) {
        this.p2 = p2;
        return this;
    }

    public FourthClass getPrivate_fourthClass() {
        return private_fourthClass;
    }
}
