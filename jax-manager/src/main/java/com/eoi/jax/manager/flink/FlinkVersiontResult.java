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

import com.eoi.jax.manager.api.VersionResult;
import com.eoi.jax.manager.exception.ProcessErrorException;
import com.eoi.jax.manager.process.ProcessOutput;
import com.eoi.jax.manager.util.StreamLineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlinkVersiontResult extends BaseFlinkJobResult implements VersionResult, StreamLineReader {
    private static final Logger logger = LoggerFactory.getLogger(FlinkVersiontResult.class);

    public static final Pattern pattern = Pattern.compile("Version: (?<version>.+?), Commit ID: (?<commit>.+?)");

    private String version;
    private String jobManager;
    private int code;
    private String message;
    private String flinkVersion;
    private String flinkCommit;

    public FlinkVersiontResult(BaseFlinkJobParam param) {
        super(param);
    }

    public String getFlinkVersion() {
        return flinkVersion;
    }

    public void setFlinkVersion(String flinkVersion) {
        this.flinkVersion = flinkVersion;
    }

    public String getFlinkCommit() {
        return flinkCommit;
    }

    public void setFlinkCommit(String flinkCommit) {
        this.flinkCommit = flinkCommit;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FlinkVersiontResult setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getJobManager() {
        return jobManager;
    }

    @Override
    public FlinkVersiontResult setJobManager(String jobManager) {
        this.jobManager = jobManager;
        return this;
    }

    @Override
    public FlinkVersiontResult deserialize(ProcessOutput output) {
        this.code = output.getCode();
        this.message = output.getCli();
        return this;
    }

    @Override
    public void readLine(String line) {
        logger.debug("[{}] {}", getUuid(), line);
        try {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                flinkVersion = matcher.group("version");
                flinkCommit = matcher.group("commit");
            }
        } catch (Exception e) {
            throw new ProcessErrorException(e);
        }
        handleLine(line);
    }

    public int getCode() {
        return code;
    }

    public FlinkVersiontResult setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public FlinkVersiontResult setMessage(String message) {
        this.message = message;
        return this;
    }
}
