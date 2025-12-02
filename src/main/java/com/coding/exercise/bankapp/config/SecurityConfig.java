package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 6 configuration using {@link SecurityFilterChain}.
 * <p>
 * Summary:
 * <ul>
 *   <li>Uses {@code authorizeHttpRequests} with {@code requestMatchers} (replaces deprecated antMatchers).</li>
 *   <li>Permits unauthenticated access to:
 *     <ul>
 *       <li>OpenAPI/Swagger endpoints: {@code /v3/api-docs/**}, {@code /swagger-ui/**}, {@code /swagger-ui.html}</li>
 *       <li>H2 console: {@code /h2-console/**}</li>
 *       <li>Lightweight health check: {@code /healthz}</li>
 *     </ul>
 *   </li>
 *   <li>Disables CSRF and frame options (required for H2 console).</li>
 *   <li>Enables HTTP Basic for all other protected endpoints.</li>
 * </ul>
 * <p>
 * Note: The application servlet context path is configured as {@code /bank-api}, so the
 * externally visible URLs are prefixed accordingly (for example,
 * {@code /bank-api/healthz}, {@code /bank-api/h2-console}).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // PUBLIC_INTERFACE
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF is disabled to simplify API interactions and allow H2 console to work with POSTs
            .csrf(csrf -> csrf.disable())
            // H2 console requires frames
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    // OpenAPI/Swagger endpoints
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    // H2 console
                    "/h2-console/**",
                    // Lightweight health endpoint under context path (/bank-api/healthz)
                    "/healthz"
                ).permitAll()
                .anyRequest().authenticated()
            )
            // Keep HTTP Basic for simplicity
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
