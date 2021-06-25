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

import com.eoi.jax.manager.api.JobStopResult;
import com.eoi.jax.manager.exception.ProcessErrorException;
import com.eoi.jax.manager.process.ProcessOutput;
import com.eoi.jax.manager.util.StreamLineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlinkJobStopResult extends BaseFlinkJobResult implements JobStopResult, StreamLineReader {
    private static final Logger logger = LoggerFactory.getLogger(FlinkJobStopResult.class);

    public static final Pattern savePointPattern1 = Pattern.compile("Savepoint stored in (?<savePoint>.+?)\\.$");
    public static final Pattern savePointPattern2 = Pattern.compile("Savepoint completed\\. Path: (?<savePoint>.+?)$");

    private String version;
    private String jobManager;
    private int code;
    private String message;
    private String savePoint;

    public FlinkJobStopResult(BaseFlinkJobParam param) {
        super(param);
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FlinkJobStopResult setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getJobManager() {
        return jobManager;
    }

    @Override
    public FlinkJobStopResult setJobManager(String jobManager) {
        this.jobManager = jobManager;
        return this;
    }

    public int getCode() {
        return code;
    }

    public FlinkJobStopResult setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public FlinkJobStopResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getSavePoint() {
        return savePoint;
    }

    public FlinkJobStopResult setSavePoint(String savePoint) {
        this.savePoint = savePoint;
        return this;
    }

    @Override
    public FlinkJobStopResult deserialize(ProcessOutput output) {
        this.code = output.getCode();
        this.message = output.getCli();
        return this;
    }

    /**
     * >>>>>>>>>>Standalone Success Result example<<<<<<<<<<
     * Draining job "6b5630be4ce83fb309f8f065431b7ece" with a savepoint.
     * Savepoint completed. Path: file:/home/jax/flink/savepoint/savepoint-9b1672-4e00f29771e0
     *
     *
     * >>>>>>>>>>YARN Success Result example<<<<<<<<<<
     * Draining job "ae792b6bb21caed49bb75d50a0cef3c8" with a savepoint.
     * 2020-03-16 17:59:33,360 INFO  org.apache.hadoop.yarn.client.RMProxy                         - Connecting to ResourceManager at node5/192.168.31.55:8032
     * 2020-03-16 17:59:33,480 INFO  org.apache.flink.yarn.cli.FlinkYarnSessionCli                 - No path for the flink jar passed. Using the location of class ...
     * 2020-03-16 17:59:33,480 INFO  org.apache.flink.yarn.cli.FlinkYarnSessionCli                 - No path for the flink jar passed. Using the location of class ...
     * 2020-03-16 17:59:33,547 INFO  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - Found application JobManager host name 'node3' and port '45271' ...
     * Savepoint completed. Path: hdfs://eoiNameService/jax/flink/savepoint/savepoint-ae792b-bda74b410bfa
     */
    @Override
    public void readLine(String line) {
        logger.info("[{}] {}", getUuid(), line);
        try {
            Matcher matcher1 = savePointPattern1.matcher(line);
            if (matcher1.find()) {
                savePoint = matcher1.group("savePoint");
            }
            Matcher matcher2 = savePointPattern2.matcher(line);
            if (matcher2.find()) {
                savePoint = matcher2.group("savePoint");
            }
        } catch (Exception e) {
            throw new ProcessErrorException(e);
        }
        handleLine(line);
    }
}
