package com.shawn.side.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {


    @Bean
    public OpenAPI openAPI() {
        final String securitySchemaName = "bearerAuth";

        return new OpenAPI()
            .info(
                new Info()
                    .title("Mini Banking API")
                    .description("Mini Banking System REST API")
                    .version("1.0.0")
            )
            .addSecurityItem(
                new SecurityRequirement()
                    .addList(securitySchemaName)
            )
            .components(
                new Components()
                    .addSecuritySchemes(
                        securitySchemaName,
                        new SecurityScheme()
                            .name(securitySchemaName)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("輸入 JWT Token（不需要加 'Bearer ' 前綴）")
                    )
            );
    }
}
