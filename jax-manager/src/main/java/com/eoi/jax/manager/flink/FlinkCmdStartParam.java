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

import com.eoi.jax.manager.api.JobStartParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlinkCmdStartParam extends BaseFlinkJobParam implements JobStartParam {
    private String startCmd;
    private String version;
    private String jobManager;

    public String getStartCmd() {
        return startCmd;
    }

    public void setStartCmd(String startCmd) {
        this.startCmd = startCmd;
    }

    @Override
    public List<String> genArguments() {
        List<String> cmds = new ArrayList<>(Arrays.asList(startCmd.split("\\s+")));
        if (cmds.get(0).endsWith("/flink")) {
            cmds.remove(0);
        }
        return cmds;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FlinkCmdStartParam setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getJobManager() {
        return jobManager;
    }

    @Override
    public FlinkCmdStartParam setJobManager(String jobManager) {
        this.jobManager = jobManager;
        return this;
    }
}
