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

package com.eoi.jax.web.service.diagnosis;

import com.eoi.jax.web.model.diagnosis.DiagnosisReq;

public class FlinkDiagnosisFactory {

    public static IFlinkDiagnosis getFlinkDiagnosis(DiagnosisReq req) {
        switch (req.getType().toLowerCase()) {
            case "cluster":
                switch (req.getFlinkVersion()) {
                    default:
                        return new ClusterDiagnosis();
                }
            case "exception":
                switch (req.getFlinkVersion()) {
                    default:
                        return new ExceptionDiagnosis();
                }
            case "task":
                switch (req.getFlinkVersion()) {
                    default:
                        return new TaskDiagnosis();
                }
            case "checkpoint":
                switch (req.getFlinkVersion()) {
                    default:
                        return new CheckpointDiagnosis();
                }
            case "tm":
                switch (req.getFlinkVersion()) {
                    default:
                        return new TaskmanagerDiagnosis();
                }
            case "log":
                switch (req.getFlinkVersion()) {
                    default:
                        return new LogDiagnosis();
                }
            default:
                return null;
        }
    }
}
