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

import com.eoi.jax.api.Builder;
import com.eoi.jax.core.JobMetaScanner;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

public class JavaJarScanner implements IJaxScanner {

    public Map<String, JobMeta> scanJob(String jarPath) throws Exception {
        try (URLClassLoader loader = new URLClassLoader(
                new URL[]{new File(jarPath).toURI().toURL()},
                this.getClass().getClassLoader())) {
            Map<String, JobMeta> result = new HashMap<>();
            ServiceLoader builders = ServiceLoader.load(Builder.class, loader);
            Iterator iterator = builders.iterator();
            while (iterator.hasNext()) {
                Object builder = iterator.next();
                Class clz = builder.getClass();
                String clzName = clz.getName();
                JobMeta jobMeta = JobMeta.copy(JobMetaScanner.getJobMetaByClass(clz))
                        .setJobName(clzName);
                result.put(clzName, jobMeta);
            }
            return result;
        }
    }
}
