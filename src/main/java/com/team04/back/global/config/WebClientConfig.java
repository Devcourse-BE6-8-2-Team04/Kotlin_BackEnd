package com.team04.back.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient weatherApiWebClient(WebClient.Builder builder) {
        WebClient build = builder.baseUrl("https://api.openweathermap.org")
                .build();
        return build;
    }
}