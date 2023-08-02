package com.fc.mini3server._core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("com.fc")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI DoctorCalAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("DoctorCal API")
                        .description("닥터캘 API 명세서")
                        .version("v0.0.1")
                );
    }
}
