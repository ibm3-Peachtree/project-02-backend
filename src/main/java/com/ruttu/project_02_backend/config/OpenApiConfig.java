package com.ruttu.project_02_backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI(){
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer"); // jwt token 사용할 때 bearer 관례적으로 붙음

        return new OpenAPI().info(
                new Info().title("Ruttu OpenAPI")
                        .description("Ruttu OpenAPI입니다")
                        .version("v0.0.0")
        ).servers(List.of(
                        new Server().url("http://localhost:8080")
                                .description("개발용 서버 주소")
                )
        ).components(
                new Components().addSecuritySchemes("JWT", securityScheme)
        );
    }
}
