package com.lms.courseservice.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)   // ← Enables @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final com.lms.security.jwt.KeycloakJwtConverter keycloakJwtConverter;
    private final com.lms.security.handler.CustomAccessDeniedHandler customAccessDeniedHandler;
    private final com.lms.security.handler.CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints — anyone can browse the course catalog
                .requestMatchers(HttpMethod.GET, "/api/courses/**", "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/courses/*/curriculum").permitAll()
                // Actuator health/info are public
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
            // Configure as OAuth2 Resource Server — validates JWT from Keycloak
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter))
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );

        return http.build();
    }
}