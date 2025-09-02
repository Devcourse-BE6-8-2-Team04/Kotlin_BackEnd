package com.team04.back.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf ->
                csrf
                    .ignoringRequestMatchers("/api/v1/users") // 회원가입은 CSRF 예외 (POST 허용)
                    .disable() // 전체 비활성화(개발용)도 가능
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/api/v1/users", // 회원가입(POST)
                        "/api/v1/reviews/**", // (필요하다면 리뷰 조회 등도 허용)
                        "/swagger-ui/**", "/v3/api-docs/**" // Swagger 허용
                    ).permitAll()
                    .anyRequest().authenticated() // 나머지는 인증 필요
            }
            .formLogin { it.disable() } // REST API 서비스라면 폼로그인 off

        return http.build()
    }
}