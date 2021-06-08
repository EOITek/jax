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

package com.eoi.jax.core;

public class JobInfo {

    public static final String TYPE_STREAMING = "streaming";
    public static final String TYPE_BATCH = "batch";
    public static final String ROLE_SOURCE = "source";
    public static final String ROLE_PROCESS = "process";
    public static final String ROLE_SINK = "sink";

    private int apiVersion;
    private String name;
    private String type;
    private String role;
    private String category;
    private String display;
    private String description;
    private String doc;
    private String icon;
    private Boolean internal;

    public int getApiVersion() {
        return apiVersion;
    }

    public JobInfo setApiVersion(int apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    public String getName() {
        return name;
    }

    public JobInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public JobInfo setType(String type) {
        this.type = type;
        return this;
    }

    public String getRole() {
        return role;
    }

    public JobInfo setRole(String role) {
        this.role = role;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public JobInfo setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getDisplay() {
        return display;
    }

    public JobInfo setDisplay(String display) {
        this.display = display;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public JobInfo setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDoc() {
        return doc;
    }

    public JobInfo setDoc(String doc) {
        this.doc = doc;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public JobInfo setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public Boolean getInternal() {
        return internal;
    }

    public JobInfo setInternal(Boolean internal) {
        this.internal = internal;
        return this;
    }
}
