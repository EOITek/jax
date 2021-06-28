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

package com.eoi.jax.web.model.opts;

import java.util.List;

public class MigrationResp {
    private List<OptsDescribe> flinkOptsList;
    private List<OptsDescribe> sparkOptsList;
    private List<OptsDescribe> marayarnOptsList;

    public List<OptsDescribe> getFlinkOptsList() {
        return flinkOptsList;
    }

    public MigrationResp setFlinkOptsList(List<OptsDescribe> flinkOptsList) {
        this.flinkOptsList = flinkOptsList;
        return this;
    }

    public List<OptsDescribe> getSparkOptsList() {
        return sparkOptsList;
    }

    public MigrationResp setSparkOptsList(List<OptsDescribe> sparkOptsList) {
        this.sparkOptsList = sparkOptsList;
        return this;
    }

    public List<OptsDescribe> getMarayarnOptsList() {
        return marayarnOptsList;
    }

    public MigrationResp setMarayarnOptsList(List<OptsDescribe> marayarnOptsList) {
        this.marayarnOptsList = marayarnOptsList;
        return this;
    }
}
