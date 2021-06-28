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
import com.eoi.jax.web.common.config.ConfigLoader;
import com.eoi.jax.web.common.config.VersionConfig;
import com.eoi.jax.web.schedule.JaxScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class SystemController implements BaseController {
    @Autowired
    private JaxScheduler jaxScheduler;

    @GetMapping("system/version")
    public ResponseResult<Map<String, String>> version() {
        return new ResponseResult<Map<String, String>>().setEntity(VersionConfig.getMap());
    }

    @GetMapping("system/config")
    public ResponseResult<Map<String, Object>> config() {
        return new ResponseResult<Map<String, Object>>().setEntity(ConfigLoader.load().toMap());
    }

    @PutMapping("system/schedule/restart")
    public ResponseResult<Void> reschedule() {
        jaxScheduler.restart();
        return new ResponseResult<>();
    }
}
