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

package com.eoi.jax.web.provider.jar;

public class JarCache {
    private String uuid;
    private Long time;
    private String path;

    public String getUuid() {
        return uuid;
    }

    public JarCache setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public Long getTime() {
        return time;
    }

    public JarCache setTime(Long time) {
        this.time = time;
        return this;
    }

    public String getPath() {
        return path;
    }

    public JarCache setPath(String path) {
        this.path = path;
        return this;
    }
}
