package com.team04.back.global.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "API documentation",
        version = "v1",
        description = "API documentation for Team04 project"
    )
)
class SpringDocConfig 
