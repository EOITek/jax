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

package com.eoi.jax.web.model.pipeline;

import com.eoi.jax.web.model.BaseQuery;

public class PipelineQueryReq extends BaseQuery {
    private PipelineFilterReq filter = new PipelineFilterReq();
    private PipelineSortReq sort = new PipelineSortReq();

    @Override
    public PipelineFilterReq getFilter() {
        return filter;
    }

    public PipelineQueryReq setFilter(PipelineFilterReq filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public PipelineSortReq getSort() {
        return sort;
    }

    public PipelineQueryReq setSort(PipelineSortReq sort) {
        this.sort = sort;
        return this;
    }
}
