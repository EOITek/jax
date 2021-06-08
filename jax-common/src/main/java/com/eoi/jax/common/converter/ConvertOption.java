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

package com.eoi.jax.common.converter;

import java.io.Serializable;
import java.math.RoundingMode;

public class ConvertOption implements Serializable {

    private Integer roundingScale;
    private String roundingMode;

    public Integer getRoundingScale() {
        return roundingScale;
    }

    public ConvertOption setRoundingScale(Integer roundingScale) {
        this.roundingScale = roundingScale;
        return this;
    }

    public String getRoundingMode() {
        return roundingMode;
    }

    public ConvertOption setRoundingMode(String roundingMode) {
        this.roundingMode = roundingMode;
        return this;
    }

    public int roundingScale() {
        return roundingScale;
    }

    public RoundingMode roundingMode() {
        if (roundingMode == null) {
            return null;
        }
        return RoundingMode.valueOf(roundingMode);
    }

    public boolean rounding() {
        return roundingScale != null;
    }
}
