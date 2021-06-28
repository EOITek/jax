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

package com.eoi.jax.web.model.jar;

import com.eoi.jax.web.dao.entity.TbJar;
import com.eoi.jax.web.model.BaseModel;

public class JarReq implements BaseModel<TbJar> {
    private String jarName;
    private String jarDescription;
    private String jarVersion;
    private String jarFile;
    private String clusterName;
    private String jarType;
    private boolean isOverride;

    public boolean isOverride() {
        return isOverride;
    }

    public JarReq setOverride(boolean override) {
        isOverride = override;
        return this;
    }

    public String getJarName() {
        return jarName;
    }

    public JarReq setJarName(String jarName) {
        this.jarName = jarName;
        return this;
    }

    public String getJarDescription() {
        return jarDescription;
    }

    public JarReq setJarDescription(String jarDescription) {
        this.jarDescription = jarDescription;
        return this;
    }

    public String getJarVersion() {
        return jarVersion;
    }

    public JarReq setJarVersion(String jarVersion) {
        this.jarVersion = jarVersion;
        return this;
    }

    public String getJarFile() {
        return jarFile;
    }

    public JarReq setJarFile(String jarFile) {
        this.jarFile = jarFile;
        return this;
    }

    public String getClusterName() {
        return clusterName;
    }

    public JarReq setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public String getJarType() {
        return jarType;
    }

    public JarReq setJarType(String jarType) {
        this.jarType = jarType;
        return this;
    }

    public TbJar toEntity(TbJar entity) {
        this.copyTo(entity);
        entity.setJobType(getJarType());
        return entity;
    }
}
