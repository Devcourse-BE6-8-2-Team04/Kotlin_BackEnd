package com.team04.back.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "API documentation",
                version = "v1",
                description = "API documentation for Team04 project"
        )
)
public class SpringDocConfig {
}
