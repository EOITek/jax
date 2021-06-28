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

package com.eoi.jax.web.controller;

import com.eoi.jax.web.common.ResponseResult;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.model.listener.SparkStateReq;
import com.eoi.jax.web.service.ListenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ListenerController extends V1Controller {
    @Autowired
    private ListenerService listenerService;

    @PutMapping("listener/spark/{pipelineName}/state")
    public ResponseResult<TbPipeline> sparkState(@PathVariable("pipelineName") String pipelineName,
                                                 @RequestBody SparkStateReq req) {
        return new ResponseResult<TbPipeline>().setEntity(listenerService.sparkState(pipelineName, req));
    }

    @PutMapping("listener/spark/{pipelineName}/event")
    public ResponseResult<TbPipeline> sparkEvent(@PathVariable("pipelineName") String pipelineName,
                                                 @RequestBody Map<String, Object> req) {
        return new ResponseResult<TbPipeline>().setEntity(listenerService.sparkEvent(pipelineName, req));
    }
}
