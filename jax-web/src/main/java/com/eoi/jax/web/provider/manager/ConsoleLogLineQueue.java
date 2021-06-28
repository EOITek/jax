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

package com.eoi.jax.web.provider.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eoi.jax.manager.api.UUID;
import com.eoi.jax.web.dao.entity.TbPipelineConsole;
import com.eoi.jax.web.dao.service.TbPipelineConsoleService;
import com.eoi.jax.web.model.manager.OpType;
import com.eoi.jax.web.model.manager.OpUUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsoleLogLineQueue extends AbstractConsoleLogLineQueue<TbPipelineConsole> {

    @Autowired
    private TbPipelineConsoleService tbPipelineConsoleService;

    @Override
    void saveBatch(List<TbPipelineConsole> batch) {
        tbPipelineConsoleService.saveBatch(batch);
    }

    @Override
    void removeBeforeTimestamp(Long timestamp) {
        tbPipelineConsoleService.remove(
                new LambdaQueryWrapper<TbPipelineConsole>()
                        .lt(TbPipelineConsole::getCreateTime, timestamp)
        );
    }

    public TbPipelineConsole console(UUID uuid, String line) {
        if (!(uuid instanceof OpUUID)) {
            return null;
        }
        OpUUID opUUID = (OpUUID) uuid;
        if (opUUID.getOpType() != OpType.START && opUUID.getOpType() != OpType.STOP) {
            return null;
        }
        TbPipelineConsole console = new TbPipelineConsole();
        console.setPipelineName(opUUID.getPipelineName());
        console.setOpType(opUUID.getOpType().code);
        console.setOpTime(opUUID.getOpTime());
        console.setCreateTime(System.currentTimeMillis());
        console.setLogContent(line);
        return console;
    }
}
