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

package com.eoi.jax.web.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eoi.jax.web.common.config.AppConfig;

public abstract class BaseQuery<T> {
    private Integer page;
    private Integer size;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public QueryWrapper<T> query() {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        BaseFilter<T> filter = getFilter();
        BaseSort<T> sort = getSort();
        if (filter != null) {
            filter.where(wrapper);
        }
        if (sort != null) {
            sort.order(wrapper);
        }
        return wrapper;
    }

    public Page<T> page() {
        if (page != null && size != null) {
            return new Page<>(page + 1L, size);
        }
        //mybatis 分页 从1开始...
        return new Page<>(1, AppConfig.MAX_SQL_ROW_COUNT);
    }

    public abstract BaseFilter<T> getFilter();

    public abstract BaseSort<T> getSort();
}
