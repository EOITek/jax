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

import com.eoi.jax.manager.api.JobStopResult;
import com.eoi.jax.manager.process.ProcessOutput;
import com.eoi.jax.manager.util.StreamLineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkJobStopResult extends BaseSparkJobResult implements JobStopResult, StreamLineReader {
    private static final Logger logger = LoggerFactory.getLogger(SparkJobStopResult.class);

    private String version;
    private String masterUrl;
    private int code;
    private String message;

    public SparkJobStopResult(BaseSparkJobParam param) {
        super(param);
    }

    public String getVersion() {
        return version;
    }

    public SparkJobStopResult setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getMasterUrl() {
        return masterUrl;
    }

    public SparkJobStopResult setMasterUrl(String masterUrl) {
        this.masterUrl = masterUrl;
        return this;
    }

    @Override
    public int getCode() {
        return code;
    }

    public SparkJobStopResult setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public SparkJobStopResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public SparkJobStopResult deserialize(ProcessOutput output) {
        this.code = output.getCode();
        this.message = output.getCli();
        return this;
    }

    @Override
    public void readLine(String line) {
        logger.info("[{}] {}", getUuid(), line);
        handleLine(line);
    }
}
