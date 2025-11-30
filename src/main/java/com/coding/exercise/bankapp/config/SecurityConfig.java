package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Spring Security 6 configuration using SecurityFilterChain.
 * - AuthorizeHttpRequests with requestMatchers.
 * - Permits:
 *   "/" (root),
 *   "/h2-console/**" (H2 Console),
 *   "/v3/api-docs/**" (OpenAPI),
 *   "/swagger-ui/**" and "/swagger-ui.html" (Swagger UI via springdoc),
 *   "/actuator/**" (Spring Boot Actuator).
 * - Disables CSRF and frame options to allow H2 console rendering.
 * - Uses HTTP Basic for other endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // PUBLIC_INTERFACE
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/h2-console/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
