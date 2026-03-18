package com.kaique.transacao_api.infrastructure.security;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaique.transacao_api.infrastructure.web.ApiErrorResponse;
import com.kaique.transacao_api.infrastructure.web.RequestIdFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter, RequestIdFilter requestIdFilter, ObjectMapper objectMapper)
            throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/transacao",
                                "/estatistica",
                                "/usuarios",
                                "/usuarios/*",
                                "/health/**",
                                "/actuator/health/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(authenticationEntryPoint(objectMapper))
                        .accessDeniedHandler(accessDeniedHandler(objectMapper)))
                .addFilterBefore(requestIdFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    private static AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
        return (request, response, authException) -> writeError(objectMapper, request, response, HttpStatus.UNAUTHORIZED, "Unauthorized",
                "Token JWT ausente ou invalido.");
    }

    private static AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
        return (request, response, accessDeniedException) -> writeError(objectMapper, request, response, HttpStatus.FORBIDDEN, "Forbidden",
                "Acesso negado.");
    }

    private static void writeError(ObjectMapper objectMapper, HttpServletRequest request, HttpServletResponse response, HttpStatus status,
            String error, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiErrorResponse body = new ApiErrorResponse(OffsetDateTime.now(), status.value(), error, message, request.getRequestURI());
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
