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

package com.eoi.jax.manager.spark;

import com.eoi.jax.manager.process.LineHandler;
import com.eoi.jax.manager.process.ProcessOutput;

public abstract class BaseSparkJobResult extends BaseSparkJob {
    private BaseSparkJobParam param;
    private LineHandler handler;

    public BaseSparkJobResult(BaseSparkJobParam param) {
        setParam(param);
    }

    public BaseSparkJobParam getParam() {
        return param;
    }

    public BaseSparkJobResult setParam(BaseSparkJobParam param) {
        this.param = param;
        setUuid(param.getUuid());
        setVersion(param.getVersion());
        setMasterUrl(param.getMasterUrl());
        return this;
    }

    public LineHandler getHandler() {
        return handler;
    }

    public BaseSparkJobResult setHandler(LineHandler handler) {
        this.handler = handler;
        return this;
    }

    abstract BaseSparkJobResult deserialize(ProcessOutput output);

    public void handleLine(String line) {
        if (handler != null) {
            handler.handleLine(getUuid(), line);
        }
    }
}
