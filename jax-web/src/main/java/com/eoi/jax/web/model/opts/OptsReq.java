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

package com.eoi.jax.web.model.opts;

import cn.hutool.core.bean.BeanUtil;
import com.eoi.jax.web.common.util.JsonUtil;
import org.springframework.beans.BeanUtils;

import java.util.List;

public abstract class OptsReq<T> {
    private String optsName;
    private String optsDescription;
    private String entryJar;
    private String entryClass;
    private String jobLib;
    private String home;
    private String version;

    private List<OptsDescribe> optsList;

    public String getOptsName() {
        return optsName;
    }

    public OptsReq setOptsName(String optsName) {
        this.optsName = optsName;
        return this;
    }

    public String getOptsDescription() {
        return optsDescription;
    }

    public OptsReq setOptsDescription(String optsDescription) {
        this.optsDescription = optsDescription;
        return this;
    }

    public String getEntryJar() {
        return entryJar;
    }

    public OptsReq setEntryJar(String entryJar) {
        this.entryJar = entryJar;
        return this;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public OptsReq setEntryClass(String entryClass) {
        this.entryClass = entryClass;
        return this;
    }

    public String getJobLib() {
        return jobLib;
    }

    public OptsReq setJobLib(String jobLib) {
        this.jobLib = jobLib;
        return this;
    }

    public String getHome() {
        return home;
    }

    public OptsReq setHome(String home) {
        this.home = home;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public OptsReq setVersion(String version) {
        this.version = version;
        return this;
    }

    public List<OptsDescribe> getOptsList() {
        return optsList;
    }

    public OptsReq setOptsList(List<OptsDescribe> optsList) {
        this.optsList = optsList;
        return this;
    }

    public T toEntity(T entity) {
        BeanUtils.copyProperties(this, entity);
        for (OptsDescribe opts : getOptsList()) {
            setProperty(entity, opts.getName(), opts.getType(), opts.getValue());
        }
        return entity;
    }

    public void setProperty(Object bean, String name, String type, Object value) {
        Object v = value;
        if (OptsValueType.LIST.isEqual(type)) {
            if (value == null) {
                v = "[]";
            } else {
                v = JsonUtil.encode(value);
            }
        }
        BeanUtil.setProperty(bean, name, v);
    }
}
