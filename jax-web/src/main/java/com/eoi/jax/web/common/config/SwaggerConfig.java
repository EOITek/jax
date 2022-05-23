package com.eoi.jax.web.common.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ConditionalOnProperty(prefix = "jax.swagger", name = "swagger-ui-open", havingValue = "true")
public class SwaggerConfig {
//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                .paths(PathSelectors.any())
//                .build();
//    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("JAX REST API")
                .version("v1")
                .description("")
                .termsOfServiceUrl("")
                .build();
    }

    @Bean
    public Docket createRestApi(){

        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("JAX REST API")
                .version("v1")
                .description("")
                .termsOfServiceUrl("")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
//                .groupName("ElectricProduction")
                .enable(true) // 默认开启
                .select()
                // apis(): 添加过滤条件;
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                .apis(RequestHandlerSelectors.basePackage("com.envisioniot.enlight.stream.ophelper.springboot.controller"))
                // paths() 控制哪些路径的api会被显示出来
                .paths(PathSelectors.any())
                .build();

    }


}
