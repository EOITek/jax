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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.manager.api.JobStartParam;
import com.eoi.jax.manager.exception.InvalidParamException;

import java.util.ArrayList;
import java.util.List;

public class SparkJobStartParam extends BaseSparkJobParam implements JobStartParam {
    private String version;
    private String masterUrl;
    private String entryClass;
    private String applicationName;
    private String applicationJar;
    private List<String> jars;
    private String driverMemory;
    private String executorMemory;
    private String driverCores;
    private String executorCores;
    // YARN-only
    private String numExecutors;
    private String yarnQueue;

    private List<String> files;
    private List<String> confList;

    private List<String> paramList;
    private List<String> pyFiles;

    private String jobFile;
    private String jobDef;
    // mode used to tell the entry program the current deploy environment
    // `standalone` and `yarn` are chosen
    private String mode;

    private Boolean enableHive;

    // flag that whether enable optimize pipeline
    private Boolean optimizePipeline;

    public List<String> getPyFiles() {
        return pyFiles;
    }

    public void setPyFiles(List<String> pyFiles) {
        this.pyFiles = pyFiles;
    }

    public Boolean getEnableHive() {
        return enableHive;
    }

    public SparkJobStartParam setEnableHive(Boolean enableHive) {
        this.enableHive = enableHive;
        return this;
    }

    public Boolean getOptimizePipeline() {
        return optimizePipeline;
    }

    public void setOptimizePipeline(Boolean optimizePipeline) {
        this.optimizePipeline = optimizePipeline;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public SparkJobStartParam setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getMasterUrl() {
        return masterUrl;
    }

    @Override
    public SparkJobStartParam setMasterUrl(String masterUrl) {
        this.masterUrl = masterUrl;
        return this;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public SparkJobStartParam setEntryClass(String entryClass) {
        this.entryClass = entryClass;
        return this;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public SparkJobStartParam setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public String getApplicationJar() {
        return applicationJar;
    }

    public SparkJobStartParam setApplicationJar(String applicationJar) {
        this.applicationJar = applicationJar;
        return this;
    }

    public List<String> getJars() {
        return jars;
    }

    public SparkJobStartParam setJars(List<String> jars) {
        this.jars = jars;
        return this;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public SparkJobStartParam setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
        return this;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public SparkJobStartParam setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
        return this;
    }

    public String getDriverCores() {
        return driverCores;
    }

    public SparkJobStartParam setDriverCores(String driverCores) {
        this.driverCores = driverCores;
        return this;
    }

    public String getExecutorCores() {
        return executorCores;
    }

    public SparkJobStartParam setExecutorCores(String executorCores) {
        this.executorCores = executorCores;
        return this;
    }

    public String getNumExecutors() {
        return numExecutors;
    }

    public SparkJobStartParam setNumExecutors(String numExecutors) {
        this.numExecutors = numExecutors;
        return this;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public SparkJobStartParam setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
        return this;
    }

    public List<String> getFiles() {
        return files;
    }

    public SparkJobStartParam setFiles(List<String> files) {
        this.files = files;
        return this;
    }

    public List<String> getConfList() {
        return confList;
    }

    public SparkJobStartParam setConfList(List<String> confList) {
        this.confList = confList;
        return this;
    }

    public List<String> getParamList() {
        return paramList;
    }

    public SparkJobStartParam setParamList(List<String> paramList) {
        this.paramList = paramList;
        return this;
    }

    public String getJobFile() {
        return jobFile;
    }

    public SparkJobStartParam setJobFile(String jobFile) {
        this.jobFile = jobFile;
        return this;
    }

    public String getJobDef() {
        return jobDef;
    }

    public SparkJobStartParam setJobDef(String jobDef) {
        this.jobDef = jobDef;
        return this;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * http://spark.apache.org/docs/latest/submitting-applications.html
     *
     * Usage: spark-submit [options] &lt;app jar | python file | R file> [app arguments]
     * Usage: spark-submit --kill [submission ID] --master [spark://...]
     * Usage: spark-submit --status [submission ID] --master [spark://...]
     * Usage: spark-submit run-example [options] example-class [example args]
     *
     * Options:
     *   --master MASTER_URL         spark://host:port, mesos://host:port, yarn,
     *                               k8s://https://host:port, or local (Default: local[*]).
     *   --deploy-mode DEPLOY_MODE   Whether to launch the driver program locally ("client") or
     *                               on one of the worker machines inside the cluster ("cluster")
     *                               (Default: client).
     *   --class CLASS_NAME          Your application's main class (for Java / Scala apps).
     *   --name NAME                 A name of your application.
     *   --jars JARS                 Comma-separated list of jars to include on the driver
     *                               and executor classpaths.
     *   --packages                  Comma-separated list of maven coordinates of jars to include
     *                               on the driver and executor classpaths. Will search the local
     *                               maven repo, then maven central and any additional remote
     *                               repositories given by --repositories. The format for the
     *                               coordinates should be groupId:artifactId:version.
     *   --exclude-packages          Comma-separated list of groupId:artifactId, to exclude while
     *                               resolving the dependencies provided in --packages to avoid
     *                               dependency conflicts.
     *   --repositories              Comma-separated list of additional remote repositories to
     *                               search for the maven coordinates given with --packages.
     *   --py-files PY_FILES         Comma-separated list of .zip, .egg, or .py files to place
     *                               on the PYTHONPATH for Python apps.
     *   --files FILES               Comma-separated list of files to be placed in the working
     *                               directory of each executor. File paths of these files
     *                               in executors can be accessed via SparkFiles.get(fileName).
     *
     *   --conf PROP=VALUE           Arbitrary Spark configuration property.
     *   --properties-file FILE      Path to a file from which to load extra properties. If not
     *                               specified, this will look for conf/spark-defaults.conf.
     *
     *   --driver-memory MEM         Memory for driver (e.g. 1000M, 2G) (Default: 1024M).
     *   --driver-java-options       Extra Java options to pass to the driver.
     *   --driver-library-path       Extra library path entries to pass to the driver.
     *   --driver-class-path         Extra class path entries to pass to the driver. Note that
     *                               jars added with --jars are automatically included in the
     *                               classpath.
     *
     *   --executor-memory MEM       Memory per executor (e.g. 1000M, 2G) (Default: 1G).
     *
     *   --proxy-user NAME           User to impersonate when submitting the application.
     *                               This argument does not work with --principal / --keytab.
     *
     *   --help, -h                  Show this help message and exit.
     *   --verbose, -v               Print additional debug output.
     *   --version,                  Print the version of current Spark.
     *
     *  Cluster deploy mode only:
     *   --driver-cores NUM          Number of cores used by the driver, only in cluster mode
     *                               (Default: 1).
     *
     *  Spark standalone or Mesos with cluster deploy mode only:
     *   --supervise                 If given, restarts the driver on failure.
     *   --kill SUBMISSION_ID        If given, kills the driver specified.
     *   --status SUBMISSION_ID      If given, requests the status of the driver specified.
     *
     *  Spark standalone and Mesos only:
     *   --total-executor-cores NUM  Total cores for all executors.
     *
     *  Spark standalone and YARN only:
     *   --executor-cores NUM        Number of cores per executor. (Default: 1 in YARN mode,
     *                               or all available cores on the worker in standalone mode)
     *
     *  YARN-only:
     *   --queue QUEUE_NAME          The YARN queue to submit to (Default: "default").
     *   --num-executors NUM         Number of executors to launch (Default: 2).
     *                               If dynamic allocation is enabled, the initial number of
     *                               executors will be at least NUM.
     *   --archives ARCHIVES         Comma separated list of archives to be extracted into the
     *                               working directory of each executor.
     *   --principal PRINCIPAL       Principal to be used to login to KDC, while running on
     *                               secure HDFS.
     *   --keytab KEYTAB             The full path to the file that contains the keytab for the
     *                               principal specified above. This keytab will be copied to
     *                               the node running the Application Master via the Secure
     *                               Distributed Cache, for renewing the login tickets and the
     *                               delegation tokens periodically.
     */
    public List<String> genArguments() {
        verify();
        List<String> arguments = new ArrayList<>();
        arguments.add("--master");
        arguments.add(masterUrl);
        arguments.add("--deploy-mode");
        arguments.add("cluster");
        arguments.add("--name");
        arguments.add(applicationName);
        arguments.add("--class");
        arguments.add(entryClass);
        if (CollUtil.isNotEmpty(jars)) {
            arguments.add("--jars");
            arguments.add(String.join(",", jars));
        }
        if (StrUtil.isNotEmpty(driverMemory)) {
            arguments.add("--driver-memory");
            arguments.add(driverMemory);
        }
        if (StrUtil.isNotEmpty(executorMemory)) {
            arguments.add("--executor-memory");
            arguments.add(executorMemory);
        }
        if (StrUtil.isNotEmpty(driverCores)) {
            arguments.add("--driver-cores");
            arguments.add(driverCores);
        }
        if (StrUtil.isNotEmpty(executorCores)) {
            if (isYarnCluster()) {
                arguments.add("--executor-cores");
                arguments.add(executorCores);
            } else {
                arguments.add("--total-executor-cores");
                arguments.add(executorCores);
            }
        }
        if (isYarnCluster()) {
            if (StrUtil.isNotEmpty(numExecutors)) {
                arguments.add("--num-executors");
                arguments.add(numExecutors);
            }
            if (StrUtil.isNotEmpty(yarnQueue)) {
                arguments.add("--queue");
                arguments.add(yarnQueue);
            }
        }
        if (CollUtil.isNotEmpty(files)) {
            arguments.add("--files");
            arguments.add(String.join(",", files));
        }
        if (CollUtil.isNotEmpty(confList)) {
            for (String conf : confList) {
                arguments.add("--conf");
                arguments.add(conf);
            }
        }
        if (CollUtil.isNotEmpty(paramList)) {
            arguments.addAll(paramList);
        }
        arguments.add(applicationJar);
        if (StrUtil.isNotEmpty(jobFile)) {
            arguments.add("--job_file");
            arguments.add(jobFile);
        }

        if (CollUtil.isNotEmpty(pyFiles)) {
            arguments.add("--py-files");
            arguments.add(String.join(",",pyFiles));
        }

        if (StrUtil.isNotEmpty(jobDef)) {
            arguments.add("--job_def");
            arguments.add(jobDef);
        }
        // mode used to tell the entry program the current deploy environment
        if (StrUtil.isNotEmpty(mode)) {
            arguments.add("--mode");
            arguments.add(mode);
        }

        if (null != enableHive && enableHive) {
            arguments.add("--enableHive");
            arguments.add("true");
        }

        // enable optimize pipeline
        if (null != optimizePipeline && optimizePipeline) {
            arguments.add("--optimizePipeline");
            arguments.add("true");
        }

        return arguments;
    }

    public void verify() {
        if (StrUtil.isEmpty(version)) {
            throw new InvalidParamException("miss version");
        }
        if (StrUtil.isEmpty(masterUrl)) {
            throw new InvalidParamException("miss masterUrl");
        }
        if (StrUtil.isEmpty(entryClass)) {
            throw new InvalidParamException("miss entryClass");
        }
        if (StrUtil.isEmpty(applicationJar)) {
            throw new InvalidParamException("miss applicationJar");
        }
    }
}
