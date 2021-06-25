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

import com.eoi.jax.manager.api.JobStartResult;
import com.eoi.jax.manager.exception.ProcessErrorException;
import com.eoi.jax.manager.process.ProcessOutput;
import com.eoi.jax.manager.util.StreamLineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SparkJobStartResult extends BaseSparkJobResult implements JobStartResult, StreamLineReader {
    private static final Logger logger = LoggerFactory.getLogger(SparkJobStartResult.class);

    public static final Pattern submissionIdPattern = Pattern.compile("Driver successfully submitted as (?<submissionId>[A-Za-z0-9_\\-]+)");
    public static final Pattern yarnIdPattern = Pattern.compile("Submitted application (?<yarnId>[A-Za-z0-9_\\-]+)");

    private String version;
    private String masterUrl;
    private int code;
    private String message;
    private String submissionId;
    private String yarnId;

    public SparkJobStartResult(BaseSparkJobParam param) {
        super(param);
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public SparkJobStartResult setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getMasterUrl() {
        return masterUrl;
    }

    @Override
    public SparkJobStartResult setMasterUrl(String masterUrl) {
        this.masterUrl = masterUrl;
        return this;
    }

    @Override
    public int getCode() {
        return code;
    }

    public SparkJobStartResult setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public SparkJobStartResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public SparkJobStartResult setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
        return this;
    }

    public String getYarnId() {
        return yarnId;
    }

    public SparkJobStartResult setYarnId(String yarnId) {
        this.yarnId = yarnId;
        return this;
    }

    public SparkJobStartResult deserialize(ProcessOutput output) {
        this.code = output.getCode();
        this.message = output.getCli();
        return this;
    }

    /**
     *  >>>>>>>>>>Standalone Success Result example<<<<<<<<<<
     *
     * 20/02/05 11:27:01.821 INFO SecurityManager : Changing view acls to: root
     * 20/02/05 11:27:01.909 INFO SecurityManager : Changing modify acls to: root
     * 20/02/05 11:27:01.909 INFO SecurityManager : Changing view acls groups to:
     * 20/02/05 11:27:01.910 INFO SecurityManager : Changing modify acls groups to:
     * 20/02/05 11:27:01.911 INFO SecurityManager : SecurityManager: authentication disabled; ui acls disabled; users  with view permissions: Set(root); groups with view ...
     * 20/02/05 11:27:02.273 INFO Utils : Successfully started service 'driverClient' on port 41493.
     * 20/02/05 11:27:02.323 INFO TransportClientFactory : Successfully created connection to /192.168.31.55:7077 after 26 ms (0 ms spent in bootstraps)
     * 20/02/05 11:27:02.551 INFO ClientEndpoint : Driver successfully submitted as driver-20200205112702-0069
     * 20/02/05 11:27:02.552 INFO ClientEndpoint : ... waiting before polling master for driver state
     * 20/02/05 11:27:07.553 INFO ClientEndpoint : ... polling master for driver state
     * 20/02/05 11:27:07.579 INFO ClientEndpoint : State of driver-20200205112702-0069 is RUNNING
     * 20/02/05 11:27:07.580 INFO ClientEndpoint : Driver running on 192.168.31.55:42726 (worker-20200110133347-192.168.31.55-42726)
     * 20/02/05 11:27:07.583 INFO ShutdownHookManager : Shutdown hook called
     * 20/02/05 11:27:07.584 INFO ShutdownHookManager : Deleting directory /tmp/spark-86ac7e15-7c13-4ccc-9e82-0c089b225c9a
     *
     *
     * 2020/06/12 13:21:58.867 WARN NativeCodeLoader: [] Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
     * Running Spark using the REST application submission protocol.
     * 2020/06/12 13:21:59.275 INFO RestSubmissionClient: [] Submitting a request to launch an application in spark://192.168.101.45:6066.
     * 2020/06/12 13:22:00.136 INFO RestSubmissionClient: [] Submission successfully created as driver-20200612132159-0003. Polling submission state...
     * 2020/06/12 13:22:00.137 INFO RestSubmissionClient: [] Submitting a request for the status of submission driver-20200612132159-0003 in spark://192.168.101.45:6066.
     * 2020/06/12 13:22:00.183 INFO RestSubmissionClient: [] State of driver driver-20200612132159-0003 is now RUNNING.
     * 2020/06/12 13:22:00.183 INFO RestSubmissionClient: [] Driver is running on worker worker-20200612115217-192.168.101.45-39922 at 192.168.101.45:39922.
     * 2020/06/12 13:22:00.202 INFO RestSubmissionClient: [] Server responded with CreateSubmissionResponse:
     * {
     *   "action" : "CreateSubmissionResponse",
     *   "message" : "Driver successfully submitted as driver-20200612132159-0003",
     *   "serverSparkVersion" : "2.4.1",
     *   "submissionId" : "driver-20200612132159-0003",
     *   "success" : true
     * }
     * 2020/06/12 13:22:00.227 INFO ShutdownHookManager: [] Shutdown hook called
     * 2020/06/12 13:22:00.228 INFO ShutdownHookManager: [] Deleting directory /tmp/spark-e274115a-f1b8-454f-b8bb-e548325137dd
     *
     *
     *
     *  >>>>>>>>>>YARN Success Result example<<<<<<<<<<
     * 20/01/20 13:30:14 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
     * 20/01/20 13:30:14 INFO client.RMProxy: Connecting to ResourceManager at node5/192.168.31.55:8032
     * 20/01/20 13:30:14 INFO yarn.Client: Requesting a new application from cluster with 5 NodeManagers
     * 20/01/20 13:30:15 INFO yarn.Client: Verifying our application has not requested more than the maximum memory capability of the cluster (24576 MB per container)
     * 20/01/20 13:30:15 INFO yarn.Client: Will allocate AM container, with 2432 MB memory including 384 MB overhead
     * 20/01/20 13:30:15 INFO yarn.Client: Setting up container launch context for our AM
     * 20/01/20 13:30:15 INFO yarn.Client: Setting up the launch environment for our AM container
     * 20/01/20 13:30:15 INFO yarn.Client: Preparing resources for our AM container
     * 20/01/20 13:30:15 WARN yarn.Client: Neither spark.yarn.jars nor spark.yarn.archive is set, falling back to uploading libraries under SPARK_HOME.
     * 20/01/20 13:30:17 INFO yarn.Client: Uploading resource file:/private/var/folders/w8/_5sbnbtd7jx1hz523xr09t9m0000gn/T/spark-e140029d-9db1-40c7-9ef9-ed101ab3d05c/...
     * 20/01/20 13:30:56 INFO yarn.Client: Uploading resource file:/Users/wangdi/Desktop/jax_home/jax/jar_dir/jax-spark-job.jar -> hdfs://eoiNameService/user/...
     * 20/01/20 13:31:17 INFO yarn.Client: Uploading resource file:/Users/wangdi/Desktop/jax_home/jax/work/wd_batch029-1579498211068.pipeline -> hdfs://eoiNameService/user/...
     * 20/01/20 13:31:17 INFO yarn.Client: Uploading resource file:/Users/wangdi/Desktop/jax_home/python/jax-algorithm.tar.gz#jax-algorithm -> hdfs://eoiNameService/user/...
     * 20/01/20 13:31:17 INFO yarn.Client: Uploading resource file:/private/var/folders/w8/_5sbnbtd7jx1hz523xr09t9m0000gn/T/spark-e140029d-9db1-40c7-9ef9-ed101ab3d05c/...
     * 20/01/20 13:31:17 INFO spark.SecurityManager: Changing view acls to: wangdi
     * 20/01/20 13:31:17 INFO spark.SecurityManager: Changing modify acls to: wangdi
     * 20/01/20 13:31:17 INFO spark.SecurityManager: Changing view acls groups to:
     * 20/01/20 13:31:17 INFO spark.SecurityManager: Changing modify acls groups to:
     * 20/01/20 13:31:17 INFO spark.SecurityManager: SecurityManager: authentication disabled; ui acls disabled; users  with view permissions: Set(wangdi);...
     * 20/01/20 13:31:18 INFO security.EsServiceCredentialProvider: Loaded EsServiceCredentialProvider
     * 20/01/20 13:31:19 INFO security.EsServiceCredentialProvider: Hadoop Security Enabled = [false]
     * 20/01/20 13:31:19 INFO security.EsServiceCredentialProvider: ES Auth Method = [SIMPLE]
     * 20/01/20 13:31:19 INFO security.EsServiceCredentialProvider: Are creds required = [false]
     * 20/01/20 13:31:19 INFO yarn.Client: Submitting application application_1578970174552_0159 to ResourceManager
     * 20/01/20 13:31:19 INFO impl.YarnClientImpl: Submitted application application_1578970174552_0159
     * 20/01/20 13:31:19 INFO yarn.Client: Application report for application_1578970174552_0159 (state: ACCEPTED)
     * 20/01/20 13:31:19 INFO yarn.Client:
     *  client token: N/A
     *  diagnostics: N/A
     *  ApplicationMaster host: N/A
     *  ApplicationMaster RPC port: -1
     *  queue: root.users.wangdi
     *  start time: 1579498279143
     *  final status: UNDEFINED
     *  tracking URL: http://node5:8088/proxy/application_1578970174552_0159/
     *  user: wangdi
     * 20/01/20 13:31:19 INFO util.ShutdownHookManager: Shutdown hook called
     * 20/01/20 13:31:19 INFO util.ShutdownHookManager: Deleting directory /private/var/folders/w8/_5sbnbtd7jx1hz523xr09t9m0000gn/T/spark-5ea3ddc4-016a-4b4c-a7de-bea4b5bfee18
     * 20/01/20 13:31:19 INFO util.ShutdownHookManager: Deleting directory /private/var/folders/w8/_5sbnbtd7jx1hz523xr09t9m0000gn/T/spark-e140029d-9db1-40c7-9ef9-ed101ab3d05c
     *
     *
     */
    @Override
    public void readLine(String line) {
        logger.info("[{}] {}", getUuid(), line);
        try {
            Matcher submissionIdMatcher = submissionIdPattern.matcher(line);
            if (submissionIdMatcher.find()) {
                submissionId = submissionIdMatcher.group("submissionId");
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
