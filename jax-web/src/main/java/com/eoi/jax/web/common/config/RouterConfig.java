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

package com.eoi.jax.web.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.File;
import java.io.IOException;

@Configuration
public class RouterConfig implements WebMvcConfigurer {
    @Autowired
    private AppConfig appConfig;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (Boolean.TRUE.equals(appConfig.jax.getCorsEnabled())) {
            registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS");
        }
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/")
                .setViewName("forward:" + ConfigLoader.load().jax.getWebsite().getIndex());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler(String.format("/%s/**",
                AppConfig.JAX_JOB_PYTHON_DIR))
                .addResourceLocations(
                        new File(ConfigLoader.load().jax.getJaxPythonDir()).toURI().toString()
                ).setCacheControl(CacheControl.noCache());

        registry.addResourceHandler(String.format("/%s/**",
                AppConfig.JAX_JOB_WORK_DIR))
                .addResourceLocations(
                        new File(ConfigLoader.load().jax.getWork()).toURI().toString()
                ).setCacheControl(CacheControl.noCache());

        registry.addResourceHandler(String.format("/%s/**",
                AppConfig.JAX_JOB_JAR_DIR))
                .addResourceLocations(
                        new File(ConfigLoader.load().jax.getJarDir()).toURI().toString()
                ).setCacheControl(CacheControl.noCache());

        registry.addResourceHandler(String.format("/%s/**",
                AppConfig.JAX_JOB_JAR_LIB))
                .addResourceLocations(
                        new File(ConfigLoader.load().jax.getJarLib()).toURI().toString()
                ).setCacheControl(CacheControl.noCache());

        registry.addResourceHandler("/**")
                .addResourceLocations(
                        new File(ConfigLoader.load().jax.getWebsite().getRoot()).toURI().toString()
                )
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath,
                                                   Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        boolean found = resourcePath.startsWith("api/") || (requestedResource.exists() && requestedResource.isReadable());
                        return found ? requestedResource :
                                new FileSystemResource(ConfigLoader.load().jax.getWebsite().getIndex());
                    }
                });


        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

    }
}
