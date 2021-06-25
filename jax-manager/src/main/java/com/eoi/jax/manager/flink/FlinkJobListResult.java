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

import com.eoi.jax.manager.api.JobListResult;
import com.eoi.jax.manager.exception.ProcessErrorException;
import com.eoi.jax.manager.process.ProcessOutput;
import com.eoi.jax.manager.util.StreamLineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlinkJobListResult extends BaseFlinkJobResult implements JobListResult, StreamLineReader {
    private static final Logger logger = LoggerFactory.getLogger(FlinkJobListResult.class);

    public static final Pattern pattern = Pattern.compile("(?<startTime>\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}:\\d{2}) : (?<jobId>.+?) : (?<jobName>.+?) \\((?<jobState>.+?)\\)");

    private String version;
    private String jobManager;
    private int code;
    private String message;
    private List<FlinkJobState> jobList = new ArrayList<>();

    public FlinkJobListResult(BaseFlinkJobParam param) {
        super(param);
    }


    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FlinkJobListResult setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getJobManager() {
        return jobManager;
    }

    @Override
    public FlinkJobListResult setJobManager(String jobManager) {
        this.jobManager = jobManager;
        return this;
    }

    public int getCode() {
        return code;
    }

    public FlinkJobListResult setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public FlinkJobListResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public List<FlinkJobState> getJobList() {
        return jobList;
    }

    public FlinkJobListResult setJobList(List<FlinkJobState> jobList) {
        this.jobList = jobList;
        return this;
    }

    @Override
    public FlinkJobListResult deserialize(ProcessOutput output) {
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
                FlinkJobState job = new FlinkJobState()
                        .setStartTime(matcher.group("startTime"))
                        .setJobId(matcher.group("jobId"))
                        .setJobName(matcher.group("jobName"))
                        .setJobState(matcher.group("jobState"));
                jobList.add(job);
            }
        } catch (Exception e) {
            throw new ProcessErrorException(e);
        }
        handleLine(line);
    }
}
