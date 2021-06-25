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

package com.eoi.jax.manager.flink;

import com.eoi.jax.manager.process.LineHandler;
import com.eoi.jax.manager.process.ProcessOutput;

public abstract class BaseFlinkJobResult extends BaseFlinkJob {
    private BaseFlinkJobParam param;
    private LineHandler handler;

    public BaseFlinkJobResult(BaseFlinkJobParam param) {
        setParam(param);
    }

    public BaseFlinkJobParam getParam() {
        return param;
    }

    public BaseFlinkJobResult setParam(BaseFlinkJobParam param) {
        this.param = param;
        setUuid(param.getUuid());
        setVersion(param.getVersion());
        setJobManager(param.getJobManager());
        return this;
    }

    public LineHandler getHandler() {
        return handler;
    }

    public BaseFlinkJobResult setHandler(LineHandler handler) {
        this.handler = handler;
        return this;
    }

    abstract BaseFlinkJobResult deserialize(ProcessOutput output);

    public void handleLine(String line) {
        if (handler != null) {
            handler.handleLine(getUuid(), line);
        }
    }
}
