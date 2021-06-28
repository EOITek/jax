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

package com.eoi.jax.web.model.job;

import com.eoi.jax.web.model.BaseQuery;

public class JobQueryReq extends BaseQuery {
    private JobFilterReq filter = new JobFilterReq();
    private JobSortReq sort = new JobSortReq();

    @Override
    public JobFilterReq getFilter() {
        return filter;
    }

    public JobQueryReq setFilter(JobFilterReq filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public JobSortReq getSort() {
        return sort;
    }

    public JobQueryReq setSort(JobSortReq sort) {
        this.sort = sort;
        return this;
    }
}
