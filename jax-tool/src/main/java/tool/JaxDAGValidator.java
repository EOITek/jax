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

package tool;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.eoi.jax.api.InvalidConfigParam;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.common.JsonUtil;
import com.eoi.jax.core.CheckConfigDAGBuilder;
import com.eoi.jax.core.CheckConfigNode;
import com.eoi.jax.core.JobBuildException;
import com.eoi.jax.core.entry.EdgeDescription;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class JaxDAGValidator {
    public static final String ALG = "alg";
    private CheckConfigDAGBuilder builder;
    private final String mode;
    private final String path;
    private final String output;

    public JaxDAGValidator(String mode, String path, String output) {
        this.mode = mode;
        this.path = path;
        this.output = output;
        this.builder = new CheckConfigDAGBuilder();
    }

    public JaxDAGValidator setBuilder(CheckConfigDAGBuilder builder) {
        this.builder = builder;
        return this;
    }

    public void run() {
        PipelineDAG dag = readPipelineDAG(path);
        ValidateResult result = validateDagResult(dag);
        writeResult(result);
    }

    private void writeResult(ValidateResult result) {
        String content;
        try {
            content = JsonUtil.encode(result);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
        FileUtil.writeString(content, output, StandardCharsets.UTF_8);
    }

    private PipelineDAG readPipelineDAG(String path) {
        try {
            String content = FileUtil.readString(path, StandardCharsets.UTF_8);
            return JsonUtil.decode(content, PipelineDAG.class);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    private ValidateResult validateDagResult(PipelineDAG dag) {
        ValidateResult result = new ValidateResult();
        try {
            validateDag(dag);
            result.setInvalid(false);
            result.setCompatible(builder.isCompatible());
        } catch (Exception e) {
            result.setInvalid(true);
            result.setMessage(e.getMessage());
            result.setStackTrace(ExceptionUtil.stacktraceToString(e, 10240));
            Throwable cause;
            if ((cause = ExceptionUtil.getCausedBy(e, JobBuildException.class)) != null) {
                result.setMessage(cause.getMessage());//提取更准确的错误信息
                result.setInvalidJobId(((JobBuildException)cause).getInvalidJobId());
            }
            if ((cause = ExceptionUtil.getCausedBy(e, JobConfigValidationException.class)) != null) {
                result.setMessage(cause.getMessage());//提取更准确的错误信息
            }
            if ((cause = ExceptionUtil.getCausedBy(e, InvalidConfigParam.class)) != null) {
                result.setMessage(cause.getMessage());//提取更准确的错误信息
                String paramName = ((InvalidConfigParam)cause).name;
                result.putInvalidJobConfig(paramName, cause.getMessage());
            }
        }
        result.setSuccess(true);
        return result;
    }

    private void validateDag(PipelineDAG dag) throws JobBuildException {
        try {
            for (EdgeDescription edge : dag.getEdges()) {
                final String from = Optional.ofNullable(edge.getFrom())
                        .orElseThrow(() -> new JobBuildException("invalid edge from NULL"));
                final String to = Optional.ofNullable(edge.getTo())
                        .orElseThrow(() -> new JobBuildException("invalid edge to NULL"));
                final int fromSlot = Optional.ofNullable(edge.getFromSlot()).orElse(0);
                final int toSlot = Optional.ofNullable(edge.getToSlot()).orElse(-1);
                final CheckConfigNode fromNode = dag.getJobs().stream().filter(i -> from.equals(i.getId())).findAny()
                        .orElseThrow(() -> new JobBuildException("cannot find job id: " + from));
                final CheckConfigNode toNode = dag.getJobs().stream().filter(i -> to.equals(i.getId())).findAny()
                        .orElseThrow(() -> new JobBuildException("cannot find job id: " + to));
                builder.putEdge(fromNode, toNode, fromSlot, toSlot);
            }
            builder.check();
        } catch (JobBuildException e) {
            throw e;
        }  catch (Throwable e) {
            throw new JobBuildException("failed to validate", e);
        }
    }
}
