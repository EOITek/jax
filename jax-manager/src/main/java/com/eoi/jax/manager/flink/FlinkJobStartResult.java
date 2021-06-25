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

import com.eoi.jax.manager.api.JobStartResult;
import com.eoi.jax.manager.exception.ProcessErrorException;
import com.eoi.jax.manager.process.ProcessOutput;
import com.eoi.jax.manager.util.StreamLineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlinkJobStartResult extends BaseFlinkJobResult implements JobStartResult, StreamLineReader {
    private static final Logger logger = LoggerFactory.getLogger(FlinkJobStartResult.class);

    public static final Pattern jobIdPattern = Pattern.compile("Job has been submitted with JobID (?<jobId>.+?)$");
    public static final Pattern yarnIdPattern = Pattern.compile("Submitted application (?<yarnId>.+?)$");

    private String version;
    private String jobManager;
    private int code;
    private String message;
    private String jobId;
    private String yarnId;

    public FlinkJobStartResult(BaseFlinkJobParam param) {
        super(param);
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FlinkJobStartResult setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getJobManager() {
        return jobManager;
    }

    @Override
    public FlinkJobStartResult setJobManager(String jobManager) {
        this.jobManager = jobManager;
        return this;
    }

    public int getCode() {
        return code;
    }

    public FlinkJobStartResult setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public FlinkJobStartResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getJobId() {
        return jobId;
    }

    public FlinkJobStartResult setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getYarnId() {
        return yarnId;
    }

    public FlinkJobStartResult setYarnId(String yarnId) {
        this.yarnId = yarnId;
        return this;
    }

    @Override
    public FlinkJobStartResult deserialize(ProcessOutput output) {
        this.code = output.getCode();
        this.message = output.getCli();
        return this;
    }

    /**
     * >>>>>>>>>>Standalone Success Result example<<<<<<<<<<
     * Starting execution of program
     * Job has been submitted with JobID da53179bf8a4e981c8dc0fbe416e8c58
     *
     *
     * >>>>>>>>>>YARN Success Result example<<<<<<<<<<
     * 2020-01-16 16:29:19,588 INFO  org.apache.hadoop.yarn.client.RMProxy                         - Connecting to ResourceManager at node5/192.168.31.55:8032
     * 2020-01-16 16:29:19,708 INFO  org.apache.flink.yarn.cli.FlinkYarnSessionCli                 - No path for the flink jar passed. Using the location of class org.apache...
     * 2020-01-16 16:29:19,708 INFO  org.apache.flink.yarn.cli.FlinkYarnSessionCli                 - No path for the flink jar passed. Using the location of class org.apache...
     * 2020-01-16 16:29:21,097 INFO  org.apache.flink.yarn.cli.FlinkYarnSessionCli                 - The argument yn is deprecated in will be ignored.
     * 2020-01-16 16:29:21,097 INFO  org.apache.flink.yarn.cli.FlinkYarnSessionCli                 - The argument yn is deprecated in will be ignored.
     * 2020-01-16 16:29:21,425 INFO  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - Cluster specification: ClusterSpecification{masterMemoryMB=2048, taskManager...
     * 2020-01-16 16:29:22,164 WARN  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - The configuration directory ('...') contains both LOG4J and Logback ...
     * 2020-01-16 16:29:57,012 INFO  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - Submitting application master application_1578970174552_0074
     * 2020-01-16 16:29:57,045 INFO  org.apache.hadoop.yarn.client.api.impl.YarnClientImpl         - Submitted application application_1578970174552_0074
     * 2020-01-16 16:29:57,045 INFO  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - Waiting for the cluster to be allocated
     * 2020-01-16 16:29:57,048 INFO  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - Deploying cluster, current state ACCEPTED
     * 2020-01-16 16:30:01,679 INFO  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - YARN application has been deployed successfully.
     * 2020-01-16 16:30:01,680 INFO  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - The Flink YARN client has been started in detached mode. In order to stop...
     * yarn application -kill application_1578970174552_0074
     * Please also note that the temporary files of the YARN session in the home directory will not be removed.
     * Job has been submitted with JobID 00d12c731611db7cc00d179fd9073d7e
     *
     */
    @Override
    public void readLine(String line) {
        logger.info("[{}] {}", getUuid(), line);
        try {
            Matcher jobIdMatcher = jobIdPattern.matcher(line);
            if (jobIdMatcher.find()) {
                jobId = jobIdMatcher.group("jobId");
            }
            Matcher yarnIdMatcher = yarnIdPattern.matcher(line);
            if (yarnIdMatcher.find()) {
                yarnId = yarnIdMatcher.group("yarnId");
            }
        } catch (Exception e) {
            throw new ProcessErrorException(e);
        }
        handleLine(line);
    }
}
