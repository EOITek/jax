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

package com.eoi.jax.web.controller;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.common.ResponseResult;
import com.eoi.jax.web.model.Paged;
import com.eoi.jax.web.model.jar.JarQueryReq;
import com.eoi.jax.web.model.jar.JarReq;
import com.eoi.jax.web.model.jar.JarResp;
import com.eoi.jax.web.service.JarService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@RestController
public class JarController extends V1Controller {

    @Autowired
    private JarService jarService;

    @ApiOperation("获取jar列表")
    @GetMapping("jar")
    public ResponseResult<List<JarResp>> list() {
        return new ResponseResult<List<JarResp>>().setEntity(jarService.listJar());
    }

    @ApiOperation("获取jar列表")
    @PostMapping("jar/list/query")
    public ResponseResult<List<JarResp>> query(@RequestBody JarQueryReq req) {
        Paged<JarResp> paged = jarService.queryJar(req);
        return new ResponseResult<List<JarResp>>().setEntity(paged.getList()).setTotal(paged.getTotal());
    }

    @ApiOperation("获取jar详情")
    @GetMapping("jar/{jarName}")
    public ResponseResult<JarResp> get(@PathVariable("jarName") String jarName) {
        return new ResponseResult<JarResp>().setEntity(jarService.getJar(jarName));
    }

    @ApiOperation("新建jar")
    @PostMapping("jar/{jarName}")
    public ResponseResult<JarResp> uploadCreate(
            @RequestParam("file") MultipartFile file,
            @PathVariable("jarName") String jarName,
            @RequestParam(value = "jarVersion", required = false) String jarVersion,
            @RequestParam(value = "jarFile", required = false) String jarFile,
            @RequestParam(value = "jarDescription", required = false) String jarDescription,
            @RequestParam(value = "clusterName", required = false) String clusterName,
            @RequestParam(value = "jarType", required = false) String jarType,
            @RequestParam(value = "isOverride", required = false, defaultValue = "true") boolean isOverride) {
        JarReq req = new JarReq()
                .setJarName(jarName)
                .setJarDescription(jarDescription)
                .setJarVersion(jarVersion)
                .setJarFile(jarFile)
                .setClusterName(clusterName)
                .setJarType(jarType)
                .setOverride(isOverride);
        return new ResponseResult<JarResp>().setEntity(jarService.createJar(file, req));
    }

    @ApiOperation("新建jar")
    @PostMapping("jar-build-in/{jarName}")
    public ResponseResult<JarResp> uploadBuildInCreate(
            @RequestParam(value = "jarBuildFile") String jarBuildFile,
            @PathVariable("jarName") String jarName,
            @RequestParam(value = "jarVersion", required = false) String jarVersion,
            @RequestParam(value = "jarFile", required = false) String jarFile,
            @RequestParam(value = "jarDescription", required = false) String jarDescription,
            @RequestParam(value = "clusterName", required = false) String clusterName,
            @RequestParam(value = "isOverride", required = false, defaultValue = "true") boolean isOverride) {
        JarReq req = new JarReq()
                .setJarName(jarName)
                .setJarDescription(jarDescription)
                .setJarVersion(jarVersion)
                .setJarFile(jarFile)
                .setClusterName(clusterName)
                .setOverride(isOverride);
        return new ResponseResult<JarResp>().setEntity(jarService.createJar(jarBuildFile, req));
    }

    @PutMapping("jar/{jarName}")
    public ResponseResult<JarResp> uploadUpdate(
            @RequestParam("file") MultipartFile file,
            @PathVariable("jarName") String jarName,
            @RequestParam(value = "jarVersion", required = false) String jarVersion,
            @RequestParam(value = "jarFile", required = false) String jarFile,
            @RequestParam(value = "jarDescription", required = false) String jarDescription,
            @RequestParam(value = "clusterName", required = false) String clusterName,
            @RequestParam(value = "jarType", required = false) String jarType,
            @RequestParam(value = "isOverride", required = false, defaultValue = "true") boolean isOverride
    ) {
        JarReq req = new JarReq()
                .setJarName(jarName)
                .setJarDescription(jarDescription)
                .setJarVersion(jarVersion)
                .setJarFile(jarFile)
                .setJarType(jarType)
                .setClusterName(clusterName)
                .setOverride(isOverride);
        return new ResponseResult<JarResp>().setEntity(jarService.updateJar(file, req));
    }

    @ApiOperation("更新jar")
    @PutMapping("jar-build-in/{jarName}")
    public ResponseResult<JarResp> uploadBuildInUpdate(
            @RequestParam(value = "jarBuildFile") String jarBuildFile,
            @PathVariable("jarName") String jarName,
            @RequestParam(value = "jarVersion", required = false) String jarVersion,
            @RequestParam(value = "jarFile", required = false) String jarFile,
            @RequestParam(value = "jarDescription", required = false) String jarDescription,
            @RequestParam(value = "clusterName", required = false) String clusterName,
            @RequestParam(value = "jarType", required = false) String jarType,
            @RequestParam(value = "isOverride", required = false, defaultValue = "true") boolean isOverride) {
        JarReq req = new JarReq()
                .setJarName(jarName)
                .setJarDescription(jarDescription)
                .setJarVersion(jarVersion)
                .setJarFile(jarFile)
                .setJarType(jarType)
                .setClusterName(clusterName)
                .setOverride(isOverride);
        return new ResponseResult<JarResp>().setEntity(jarService.updateJar(jarBuildFile, req));
    }

    @ApiOperation("删除jar")
    @DeleteMapping("jar/{jarName}")
    public ResponseResult<JarResp> delete(@PathVariable("jarName") String jarName) {
        return new ResponseResult<JarResp>().setEntity(jarService.deleteJar(jarName));
    }

    @ApiOperation("archive jar")
    @GetMapping("jar-archive")
    public void archive(@RequestParam(value = "jars", required = true) String jars,
                        @RequestParam(value = "filename", required = false) String filename,
                        final HttpServletResponse response) throws IOException {
        if (StrUtil.isBlank(filename)) {
            filename = String.format("jax-archive-%s.jar", System.currentTimeMillis());
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename=" + filename);
        jarService.archive(jars, response.getOutputStream());
    }

    @ApiOperation("build-in jar")
    @GetMapping("jar-build-in")
    public ResponseResult<List<String>> buildIns() {
        return new ResponseResult<List<String>>().setEntity(jarService.listBuildIns());
    }


}
