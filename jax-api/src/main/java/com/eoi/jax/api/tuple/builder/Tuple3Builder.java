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

package com.eoi.jax.api.tuple.builder;

import com.eoi.jax.api.tuple.Tuple3;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder class for {@link Tuple3}.
 *
 * @param <T0> The type of field 0
 * @param <T1> The type of field 1
 * @param <T2> The type of field 2
 */
public class Tuple3Builder<T0, T1, T2> {

    private List<Tuple3<T0, T1, T2>> tuples = new ArrayList<>();

    public Tuple3Builder<T0, T1, T2> add(T0 value0, T1 value1, T2 value2) {
        tuples.add(new Tuple3<>(value0, value1, value2));
        return this;
    }

    @SuppressWarnings("unchecked")
    public Tuple3<T0, T1, T2>[] build() {
        return tuples.toArray(new Tuple3[tuples.size()]);
    }
}
