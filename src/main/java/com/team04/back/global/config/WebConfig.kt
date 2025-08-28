package com.team04.back.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**").apply {
            allowedOrigins("http://localhost:3000") // localhost:3000 출처 허용
            allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 허용할 HTTP method
            allowedHeaders("*") // 모든 헤더 허용
            allowCredentials(true) // 쿠키 인증 요청 허용
            maxAge(3600)
        }
    }
}
