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

package com.eoi.jax.web.common;

/**
 * A tuple with 2 fields. Tuples are strongly typed; each field may be of a separate type.
 *
 * @param <T0> The type of field 0
 * @param <T1> The type of field 1
 */
public class Tuple<T0, T1> {
    /** Field 0 of the tuple. */
    public T0 f0;
    /** Field 1 of the tuple. */
    public T1 f1;

    /**
     * Creates a new tuple where all fields are null.
     */
    public Tuple() {
    }

    /**
     * Creates a new tuple and assigns the given values to the tuple's fields.
     *
     * @param value0 The value for field 0
     * @param value1 The value for field 1
     */
    public Tuple(T0 value0, T1 value1) {
        this.f0 = value0;
        this.f1 = value1;
    }

    public T0 getF0() {
        return f0;
    }

    public Tuple setF0(T0 f0) {
        this.f0 = f0;
        return this;
    }

    public T1 getF1() {
        return f1;
    }

    public Tuple setF1(T1 f1) {
        this.f1 = f1;
        return this;
    }

    public static <T0, T1> Tuple<T0, T1> of(T0 value0, T1 value1) {
        return new Tuple<>(value0, value1);
    }
}
