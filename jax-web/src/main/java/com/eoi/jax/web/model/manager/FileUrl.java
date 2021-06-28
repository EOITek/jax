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

package com.eoi.jax.web.model.manager;

public class FileUrl {
    //文件名
    private String name;
    //绝对路径
    private String path;
    //http url
    private String url;

    public String getName() {
        return name;
    }

    public FileUrl setName(String name) {
        this.name = name;
        return this;
    }

    public String getPath() {
        return path;
    }

    public FileUrl setPath(String path) {
        this.path = path;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public FileUrl setUrl(String url) {
        this.url = url;
        return this;
    }
}
