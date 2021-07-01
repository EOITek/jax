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
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.common.JsonUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JaxJobScanner {
    public static final String PYTHON = "python";
    private final String mode;
    private final String path;
    private final String output;
    private final IJaxScanner scanner;

    public JaxJobScanner(String mode, String path, String output) {
        this.mode = mode;
        this.path = path;
        this.output = output;
        if (StrUtil.equalsIgnoreCase(PYTHON, mode)) {
            scanner = new PythonZipScanner();
        } else {
            scanner = new JavaJarScanner();
        }
        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException(String.format("Empty path '%s'", path));
        }
        if (StrUtil.isBlank(output)) {
            throw new IllegalArgumentException(String.format("Empty output '%s'", output));
        }
    }

    public void run() {
        ScanResult result = new ScanResult();
        try {
            Map<String, JobMeta> jobMetaMap = scanner.scanJob(path);
            result.setJobs(jobMetaMap);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setStackTrace(ExceptionUtil.stacktraceToString(e, 10240));
        }
        String content;
        try {
            content = JsonUtil.encode(result);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
        FileUtil.writeString(content, output, StandardCharsets.UTF_8);
    }
}
