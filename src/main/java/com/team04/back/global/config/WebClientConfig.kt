package com.team04.back.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    fun weatherApiWebClient(builder: WebClient.Builder): WebClient {
        val build = builder.baseUrl("https://api.openweathermap.org")
            .build()
        return build
    }
}