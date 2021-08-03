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

package com.eoi.jax.tool;

import cn.hutool.core.io.IoUtil;
import com.eoi.jax.common.JsonUtil;
import com.eoi.jax.common.PythonHelper;
import com.eoi.jax.core.JobInfo;
import com.eoi.jax.core.JobMetaScanner;
import com.eoi.jax.core.PythonJobInfo;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PythonZipScanner implements IJaxScanner {

    @Override
    public Map<String, JobMeta> scanJob(String path) throws Exception {
        try (FileInputStream stream = new FileInputStream(path)) {
            return scanJob(stream);
        }
    }

    public Map<String, JobMeta> scanJob(InputStream stream) throws Exception {
        Map<String, JobMeta> result = new HashMap<>();
        List<PythonJobInfo> infoList = readConfig(stream);
        for (PythonJobInfo info : infoList) {
            com.eoi.jax.core.JobMeta meta = JobMetaScanner.getJobMetaByPythonJobInfo(info);
            String jobPrefix = PythonHelper.PYTHON_VIRTUAL_PACKAGE_FLINK;
            if (JobInfo.TYPE_BATCH.equals(meta.jobInfo.getType())) {
                jobPrefix = PythonHelper.PYTHON_VIRTUAL_PACKAGE_SPARK;
            }
            String clzName = jobPrefix + info.getAlgModuleName() + "." + info.getAlgClassName();
            result.put(clzName, JobMeta.copy(meta).setJobName(clzName));
        }
        return result;
    }

    public List<PythonJobInfo> readConfig(InputStream stream) throws Exception {
        try (ZipInputStream zip = new ZipInputStream(stream)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().equals(PythonHelper.CONF_FILE)) {
                    String content = IoUtil.read(zip, StandardCharsets.UTF_8);
                    return JsonUtil.decode(
                            content,
                            new TypeReference<List<PythonJobInfo>>() {}
                    );
                }
            }
        }
        throw new JobMetaScanner.JobMetaScanException("could not find config " + PythonHelper.CONF_FILE);
    }
}
