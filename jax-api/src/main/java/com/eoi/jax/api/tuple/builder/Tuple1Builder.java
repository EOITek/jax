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

import com.eoi.jax.api.tuple.Tuple1;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder class for {@link Tuple1}.
 *
 * @param <T0> The type of field 0
 */
public class Tuple1Builder<T0> {

    private List<Tuple1<T0>> tuples = new ArrayList<>();

    public Tuple1Builder<T0> add(T0 value0) {
        tuples.add(new Tuple1<>(value0));
        return this;
    }

    @SuppressWarnings("unchecked")
    public Tuple1<T0>[] build() {
        return tuples.toArray(new Tuple1[tuples.size()]);
    }
}
