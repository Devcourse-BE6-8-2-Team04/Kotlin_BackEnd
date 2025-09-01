package com.team04.back.global.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.team04.back.standard.util.Ut
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class AppConfig(
    private val environment: Environment,
    private val objectMapper: ObjectMapper
) {

//    @Bean
//    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @PostConstruct
    fun init() {
        // Ut.json에 Spring의 ObjectMapper 연결
        Ut.json.objectMapper = objectMapper
    }

    fun isDev(): Boolean = environment.matchesProfiles("dev")
    fun isTest(): Boolean = environment.matchesProfiles("test")
    fun isProd(): Boolean = environment.matchesProfiles("prod")
    fun isNotProd(): Boolean = !isProd()
}

