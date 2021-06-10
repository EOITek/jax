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

package com.eoi.jax.core.test;

import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.api.annotation.Shift;

public class TestProcessJob2Config {

    @Parameter(
            label = "first2"
    )
    private String first;

    @Parameter(
            label = "second"
    )
    private String second;

    @Parameter(
            label = "third"
    )
    private String third;

    @Parameter(
            label =  "fourth"
    )
    private String fourth;

    @Shift
    private InnerConfig innerConfig;

    public static class InnerConfig {

        @Parameter(
                label = "p3",
                optional = true
        )
        private Long p33;

        @Parameter(
                label = "p4",
                optional = true
        )
        private Double p4;

        @Parameter(
                label = "p1",
                optional = true
        )
        private String p1;

        @Parameter(
                label = "p2",
                optional = true
        )
        private Long p2;

    }
}
