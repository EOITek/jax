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

package com.eoi.jax.web.provider;

import com.eoi.jax.manager.process.IProcessRunner;
import com.eoi.jax.web.provider.cluster.Cluster;

import java.util.Map;

public interface ProcessRunnerProvider {
    IProcessRunner flinkCmdProcessRunner(String cmd, Cluster cluster);

    IProcessRunner flinkProcessRunner(Cluster cluster);

    IProcessRunner sparkProcessRunner(Cluster cluster);

    IProcessRunner yarnProcessRunner(Cluster cluster);

    IProcessRunner jaxToolRunner(Map<String, String> environment);
}
