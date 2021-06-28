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

package com.eoi.jax.web.dao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eoi.jax.web.dao.entity.TbJob;
import com.eoi.jax.web.dao.mapper.TbJobMapper;
import com.eoi.jax.web.dao.service.TbJobService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbJobServiceImpl extends ServiceImpl<TbJobMapper, TbJob> implements TbJobService {

    @Override
    public List<TbJob> listWithoutIconDoc() {
        return list(new LambdaQueryWrapper<TbJob>()
                .select(TbJob.class, o ->
                        !"doc".equals(o.getColumn()) && !"icon".equals(o.getColumn())
                )
        );
    }

    @Override
    public IPage<TbJob> pageWithoutIconDoc(IPage<TbJob> page, QueryWrapper<TbJob> queryWrapper) {
        queryWrapper.select(TbJob.class, o ->
                !"doc".equals(o.getColumn()) && !"icon".equals(o.getColumn())
        );
        return page(page, queryWrapper);
    }
}
