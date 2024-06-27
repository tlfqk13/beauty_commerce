package com.example.sampleroad.common.config;

import com.example.sampleroad.jwt.UserDetailsImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    @Profile("local") // 이것은 "local"이 아닌 프로필을 의미합니다 (즉, real1 및 real2 모두 포함).
    public Docket apiDocumentation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .useDefaultResponseMessages(false)
                .apiInfo(metaData())
                .ignoredParameterTypes(AuthenticationPrincipal.class, Pageable.class, UserDetailsImpl.class)
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()));
    }

    @Bean
    @Profile("!local")
    public Docket apiDisabled() {
        return new Docket(DocumentationType.SWAGGER_2).enable(false);
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("SampleRoad REST API")
                .description("SampleRoad")
                .version("0.1.0")
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return springfox
                .documentation
                .spi.service
                .contexts
                .SecurityContext
                .builder()
                .securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }
}