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

import cn.hutool.core.util.StrUtil;

import java.util.List;
import java.util.stream.Collectors;

public class ClassPath {
    private String name;
    private List<String> paths;

    public ClassPath(String name, List<String> paths) {
        this.name = name;
        this.paths = paths;
    }

    public String getName() {
        return name;
    }

    public List<String> getPaths() {
        return paths;
    }

    public String genClassPath() {
        return StrUtil.join(":", getPaths().stream().sorted().collect(Collectors.toList()));
    }

    /**
     * 只关注paths字段
     */
    @Override
    public String toString() {
        return genClassPath();
    }

    /**
     * 只关注paths字段
     */
    @Override
    public int hashCode() {
        return (genClassPath()).hashCode();
    }

    /**
     * 只关注paths字段
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ClassPath)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return this.hashCode() == obj.hashCode();
    }
}
