package com.team04.back.global.security

import com.team04.back.global.rsData.RsData
import com.team04.back.standard.util.Ut
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val customAuthenticationFilter: CustomAuthenticationFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/favicon.ico", "/h2-console/**").permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/*/posts/{id:\\d+}",
                        "/api/*/posts",
                        "/api/*/posts/{postId:\\d+}/comments",
                        "/api/*/posts/{postId:\\d+}/comments/{id:\\d+}"
                    ).permitAll()
                    .requestMatchers("/api/*/members/login", "/api/*/members/logout").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/*/members").permitAll()
                    .requestMatchers("/api/*/adm/**").hasRole("ADMIN")
                    .requestMatchers("/api/*/**").authenticated()
                    .anyRequest().permitAll()
            }
            .headers { it.frameOptions { it.sameOrigin() } }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement { it.disable() }
            .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling {
                it.authenticationEntryPoint { _, response, _ ->
                    response.contentType = "application/json;charset=UTF-8"
                    response.status = 401
                    response.writer.write(Ut.json.toString(RsData<Void>("401-1", "로그인 후 이용해주세요.")))
                }
                it.accessDeniedHandler { _, response, _ ->
                    response.contentType = "application/json;charset=UTF-8"
                    response.status = 403
                    response.writer.write(Ut.json.toString(RsData<Void>("403-1", "권한이 없습니다.")))
                }
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("https://cdpn.io", "http://localhost:3000")
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE")
            allowCredentials = true
            allowedHeaders = listOf("*")
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/**", configuration)
        }
    }
}
