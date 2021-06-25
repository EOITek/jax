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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.manager.api.JobStartParam;
import com.eoi.jax.manager.exception.InvalidParamException;

import java.util.ArrayList;
import java.util.List;

public class FlinkJobStartParam extends BaseFlinkJobParam implements JobStartParam {
    private String version;
    private String jobManager;
    private String savePoint;
    private Boolean allowNonRestoredState;
    private List<String> classpath;
    private String entryClass;
    private String parallelism;
    private List<String> yarnDefinitions;
    private String yarnTaskManagerNum;
    private String yarnSlots;
    private String yarnJobManagerMemory;
    private String yarnTaskManagerMemory;
    private String yarnQueue;
    private String yarnName;
    private List<String> paramList;
    private String jarPath;
    private String jobFile;
    private String jobDef;
    private boolean applicationMode;


    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FlinkJobStartParam setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getJobManager() {
        return jobManager;
    }

    @Override
    public FlinkJobStartParam setJobManager(String jobManager) {
        this.jobManager = jobManager;
        return this;
    }

    public String getSavePoint() {
        return savePoint;
    }

    public FlinkJobStartParam setSavePoint(String savePoint) {
        this.savePoint = savePoint;
        return this;
    }

    public Boolean getAllowNonRestoredState() {
        return allowNonRestoredState;
    }

    public FlinkJobStartParam setAllowNonRestoredState(Boolean allowNonRestoredState) {
        this.allowNonRestoredState = allowNonRestoredState;
        return this;
    }

    public List<String> getClasspath() {
        return classpath;
    }

    public FlinkJobStartParam setClasspath(List<String> classpath) {
        this.classpath = classpath;
        return this;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public FlinkJobStartParam setEntryClass(String entryClass) {
        this.entryClass = entryClass;
        return this;
    }

    public String getParallelism() {
        return parallelism;
    }

    public FlinkJobStartParam setParallelism(String parallelism) {
        this.parallelism = parallelism;
        return this;
    }

    public List<String> getYarnDefinitions() {
        return yarnDefinitions;
    }

    public FlinkJobStartParam setYarnDefinitions(List<String> yarnDefinitions) {
        this.yarnDefinitions = yarnDefinitions;
        return this;
    }

    public String getYarnTaskManagerNum() {
        return yarnTaskManagerNum;
    }

    public FlinkJobStartParam setYarnTaskManagerNum(String yarnTaskManagerNum) {
        this.yarnTaskManagerNum = yarnTaskManagerNum;
        return this;
    }

    public String getYarnSlots() {
        return yarnSlots;
    }

    public FlinkJobStartParam setYarnSlots(String yarnSlots) {
        this.yarnSlots = yarnSlots;
        return this;
    }

    public String getYarnJobManagerMemory() {
        return yarnJobManagerMemory;
    }

    public FlinkJobStartParam setYarnJobManagerMemory(String yarnJobManagerMemory) {
        this.yarnJobManagerMemory = yarnJobManagerMemory;
        return this;
    }

    public String getYarnTaskManagerMemory() {
        return yarnTaskManagerMemory;
    }

    public FlinkJobStartParam setYarnTaskManagerMemory(String yarnTaskManagerMemory) {
        this.yarnTaskManagerMemory = yarnTaskManagerMemory;
        return this;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public FlinkJobStartParam setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
        return this;
    }

    public String getYarnName() {
        return yarnName;
    }

    public FlinkJobStartParam setYarnName(String yarnName) {
        this.yarnName = yarnName;
        return this;
    }

    public List<String> getParamList() {
        return paramList;
    }

    public FlinkJobStartParam setParamList(List<String> paramList) {
        this.paramList = paramList;
        return this;
    }

    public String getJarPath() {
        return jarPath;
    }

    public FlinkJobStartParam setJarPath(String jarPath) {
        this.jarPath = jarPath;
        return this;
    }

    public String getJobFile() {
        return jobFile;
    }

    public FlinkJobStartParam setJobFile(String jobFile) {
        this.jobFile = jobFile;
        return this;
    }

    public String getJobDef() {
        return jobDef;
    }

    public FlinkJobStartParam setJobDef(String jobDef) {
        this.jobDef = jobDef;
        return this;
    }

    public boolean isApplicationMode() {
        return applicationMode;
    }

    public void setApplicationMode(boolean applicationMode) {
        this.applicationMode = applicationMode;
    }

    /**
     * https://ci.apache.org/projects/flink/flink-docs-stable/ops/cli.html
     *
     * ./flink  &lt;ACTION> [OPTIONS] [ARGUMENTS]
     *
     * The following actions are available:
     *
     * Action "run" compiles and runs a program.
     *
     *   Syntax: run [OPTIONS]  &lt;jar-file>  &lt;arguments>
     *   "run" action options:
     *      -c,--class  &lt;classname>               Class with the program entry point
     *                                           ("main()" method or "getPlan()"
     *                                           method). Only needed if the JAR file
     *                                           does not specify the class in its
     *                                           manifest.
     *      -C,--classpath  &lt;url>                 Adds a URL to each user code
     *                                           classloader  on all nodes in the
     *                                           cluster. The paths must specify a
     *                                           protocol (e.g. file://) and be
     *                                           accessible on all nodes (e.g. by means
     *                                           of a NFS share). You can use this
     *                                           option multiple times for specifying
     *                                           more than one URL. The protocol must
     *                                           be supported by the {@link
     *                                           java.net.URLClassLoader}.
     *      -d,--detached                        If present, runs the job in detached
     *                                           mode
     *      -n,--allowNonRestoredState           Allow to skip savepoint state that
     *                                           cannot be restored. You need to allow
     *                                           this if you removed an operator from
     *                                           your program that was part of the
     *                                           program when the savepoint was
     *                                           triggered.
     *      -p,--parallelism  &lt;parallelism>       The parallelism with which to run the
     *                                           program. Optional flag to override the
     *                                           default value specified in the
     *                                           configuration.
     *      -py,--python  &lt;python>                Python script with the program entry
     *                                           point. The dependent resources can be
     *                                           configured with the `--pyFiles`
     *                                           option.
     *      -pyfs,--pyFiles  &lt;pyFiles>            Attach custom python files for job.
     *                                           Comma can be used as the separator to
     *                                           specify multiple files. The standard
     *                                           python resource file suffixes such as
     *                                           .py/.egg/.zip are all supported.(eg:
     *                                           --pyFiles
     *                                           file:///tmp/myresource.zip,hdfs:///$na
     *                                           menode_address/myresource2.zip)
     *      -pym,--pyModule  &lt;pyModule>           Python module with the program entry
     *                                           point. This option must be used in
     *                                           conjunction with `--pyFiles`.
     *      -q,--sysoutLogging                   If present, suppress logging output to
     *                                           standard out.
     *      -s,--fromSavepoint  &lt;savepointPath>   Path to a savepoint to restore the job
     *                                           from (for example
     *                                           hdfs:///flink/savepoint-1537).
     *      -sae,--shutdownOnAttachedExit        If the job is submitted in attached
     *                                           mode, perform a best-effort cluster
     *                                           shutdown when the CLI is terminated
     *                                           abruptly, e.g., in response to a user
     *                                           interrupt, such as typing Ctrl + C.
     *   Options for default mode:
     *      -m,--jobmanager  &lt;arg>           Address of the JobManager (master) to which
     *                                      to connect. Use this flag to connect to a
     *                                      different JobManager than the one specified
     *                                      in the configuration.
     *      -z,--zookeeperNamespace  &lt;arg>   Namespace to create the Zookeeper sub-paths
     *                                      for high availability mode
     *
     *
     *
     * Action "info" shows the optimized execution plan of the program (JSON).
     *
     *   Syntax: info [OPTIONS]  &lt;jar-file>  &lt;arguments>
     *   "info" action options:
     *      -c,--class  &lt;classname>           Class with the program entry point
     *                                       ("main()" method or "getPlan()" method).
     *                                       Only needed if the JAR file does not
     *                                       specify the class in its manifest.
     *      -p,--parallelism  &lt;parallelism>   The parallelism with which to run the
     *                                       program. Optional flag to override the
     *                                       default value specified in the
     *                                       configuration.
     *
     *
     * Action "list" lists running and scheduled programs.
     *
     *   Syntax: list [OPTIONS]
     *   "list" action options:
     *      -r,--running     Show only running programs and their JobIDs
     *      -s,--scheduled   Show only scheduled programs and their JobIDs
     *   Options for default mode:
     *      -m,--jobmanager  &lt;arg>           Address of the JobManager (master) to which
     *                                      to connect. Use this flag to connect to a
     *                                      different JobManager than the one specified
     *                                      in the configuration.
     *      -z,--zookeeperNamespace  &lt;arg>   Namespace to create the Zookeeper sub-paths
     *                                      for high availability mode
     *
     *
     *
     * Action "stop" stops a running program with a savepoint (streaming jobs only).
     *
     *   Syntax: stop [OPTIONS] &lt;Job ID>
     *   "stop" action options:
     *      -d,--drain                           Send MAX_WATERMARK before taking the
     *                                           savepoint and stopping the pipelne.
     *      -p,--savepointPath  &lt;savepointPath>   Path to the savepoint (for example
     *                                           hdfs:///flink/savepoint-1537). If no
     *                                           directory is specified, the configured
     *                                           default will be used
     *                                           ("state.savepoints.dir").
     *   Options for default mode:
     *      -m,--jobmanager  &lt;arg>           Address of the JobManager (master) to which
     *                                      to connect. Use this flag to connect to a
     *                                      different JobManager than the one specified
     *                                      in the configuration.
     *      -z,--zookeeperNamespace  &lt;arg>   Namespace to create the Zookeeper sub-paths
     *                                      for high availability mode
     *
     *
     *
     * Action "cancel" cancels a running program.
     *
     *   Syntax: cancel [OPTIONS] &lt;Job ID>
     *   "cancel" action options:
     *      -s,--withSavepoint  &lt;targetDirectory>   **DEPRECATION WARNING**: Cancelling
     *                                             a job with savepoint is deprecated.
     *                                             Use "stop" instead.
     *                                             Trigger savepoint and cancel job.
     *                                             The target directory is optional. If
     *                                             no directory is specified, the
     *                                             configured default directory
     *                                             (state.savepoints.dir) is used.
     *   Options for default mode:
     *      -m,--jobmanager  &lt;arg>           Address of the JobManager (master) to which
     *                                      to connect. Use this flag to connect to a
     *                                      different JobManager than the one specified
     *                                      in the configuration.
     *      -z,--zookeeperNamespace  &lt;arg>   Namespace to create the Zookeeper sub-paths
     *                                      for high availability mode
     *
     *
     *
     * Action "savepoint" triggers savepoints for a running job or disposes existing ones.
     *
     *   Syntax: savepoint [OPTIONS] &lt;Job ID> [&lt;target directory>]
     *   "savepoint" action options:
     *      -d,--dispose  &lt;arg>       Path of savepoint to dispose.
     *      -j,--jarfile  &lt;jarfile>   Flink program JAR file.
     *   Options for default mode:
     *      -m,--jobmanager  &lt;arg>           Address of the JobManager (master) to which
     *                                      to connect. Use this flag to connect to a
     *                                      different JobManager than the one specified
     *                                      in the configuration.
     *      -z,--zookeeperNamespace  &lt;arg>   Namespace to create the Zookeeper sub-paths
     *                                      for high availability mode
     */
    @Override
    public List<String> genArguments() {
        verify();
        List<String> arguments = new ArrayList<>();
        if (!applicationMode) {
            arguments.add("run");
            arguments.add("-m");
            arguments.add(jobManager);
        } else {
            arguments.add("run-application");
            if (compareVersion("1.11.0") < 0) {
                throw new InvalidParamException("application mode not supported for version:" + getVersion());
            } else {
                //参考： https://ci.apache.org/projects/flink/flink-docs-release-1.12/deployment/cli.html#selecting-deployment-targets
                if (isYarnCluster()) {
                    arguments.add("-t");
                    arguments.add("yarn-application");
                } else {
                    // TODO: support kubernetes-application
                    throw new InvalidParamException("application mode not supported for standalone");
                }
            }
        }
        arguments.add("-d");

        if (StrUtil.isNotEmpty(parallelism)) {
            arguments.add("-p");
            arguments.add(String.valueOf(parallelism));
        }
        if (StrUtil.isNotEmpty(savePoint)) {
            arguments.add("-s");
            arguments.add(savePoint);
        }
        if (Boolean.TRUE.equals(allowNonRestoredState)) {
            arguments.add("-n");
        }
        if (CollUtil.isNotEmpty(classpath)) {
            for (String cp : classpath) {
                arguments.add("-C");
                arguments.add(cp);
            }
        }
        if (isYarnCluster()) {
            if (!applicationMode) {
                if (CollUtil.isNotEmpty(yarnDefinitions)) {
                    for (String yarDefinition : yarnDefinitions) {
                        arguments.add("-yD");
                        arguments.add(yarDefinition);
                    }
                }
                // flink >= 1.10.0 not support -yn
                if (compareVersion("1.10.0") < 0) {
                    if (StrUtil.isNotEmpty(yarnTaskManagerNum)) {
                        arguments.add("-yn");
                        arguments.add(String.valueOf(yarnTaskManagerNum));
                    }
                }
                if (StrUtil.isNotEmpty(yarnSlots)) {
                    arguments.add("-ys");
                    arguments.add(String.valueOf(yarnSlots));
                }
                if (StrUtil.isNotEmpty(yarnJobManagerMemory)) {
                    arguments.add("-yjm");
                    arguments.add(yarnJobManagerMemory);
                }
                if (StrUtil.isNotEmpty(yarnTaskManagerMemory)) {
                    arguments.add("-ytm");
                    arguments.add(yarnTaskManagerMemory);
                }
                if (StrUtil.isNotEmpty(yarnQueue)) {
                    arguments.add("-yqu");
                    arguments.add(yarnQueue);
                }
                if (StrUtil.isNotEmpty(yarnName)) {
                    arguments.add("-ynm");
                    arguments.add(yarnName);
                }
            } else {
                // flink(version >= 1.11.0) application mode
                if (compareVersion("1.11.0") < 0) {
                    throw new InvalidParamException("application mode not supported for version:" + getVersion());
                }
                // -yD key=value 改为 -Dkey=value
                if (CollUtil.isNotEmpty(yarnDefinitions)) {
                    for (String yarnDefinition : yarnDefinitions) {
                        arguments.add("-D" + yarnDefinition);
                    }
                }
                // 默认每个Taskmanager 一个slot
                arguments.add("-Dtaskmanager.numberOfTaskSlots=1");
                if (StrUtil.isNotEmpty(yarnJobManagerMemory)) {
                    if (NumberUtil.isNumber(yarnJobManagerMemory)) {
                        yarnJobManagerMemory += "m";
                    }
                    arguments.add("-Djobmanager.memory.process.size=" + yarnJobManagerMemory);
                }
                if (StrUtil.isNotEmpty(yarnTaskManagerMemory)) {
                    if (NumberUtil.isNumber(yarnTaskManagerMemory)) {
                        yarnTaskManagerMemory += "m";
                    }
                    arguments.add("-Dtaskmanager.memory.process.size=" + yarnTaskManagerMemory);
                }
                if (StrUtil.isNotEmpty(yarnQueue)) {
                    arguments.add("-Dyarn.application.queue=" + yarnQueue);
                }
                if (StrUtil.isNotEmpty(yarnName)) {
                    arguments.add("-Dyarn.application.name=" + yarnName);
                }
            }
        }
        arguments.add("-c");
        arguments.add(entryClass);
        if (CollUtil.isNotEmpty(paramList)) {
            arguments.addAll(paramList);
        }
        arguments.add(jarPath);
        if (StrUtil.isNotEmpty(jobFile)) {
            arguments.add("--job_file");
            arguments.add(jobFile);
        }
        if (StrUtil.isNotEmpty(jobDef)) {
            arguments.add("--job_def");
            arguments.add(jobDef);
        }
        return arguments;
    }

    public void verify() {
        if (StrUtil.isEmpty(version)) {
            throw new InvalidParamException("miss version");
        }
        if (StrUtil.isEmpty(jobManager)) {
            throw new InvalidParamException("miss jobManager");
        }
        if (StrUtil.isEmpty(entryClass)) {
            throw new InvalidParamException("miss entryClass");
        }
        if (StrUtil.isEmpty(jarPath)) {
            throw new InvalidParamException("miss jarPath");
        }
    }
}
