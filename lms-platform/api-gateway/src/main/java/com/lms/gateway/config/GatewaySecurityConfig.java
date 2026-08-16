package com.lms.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class GatewaySecurityConfig {

    /**
     * Blocks /api/internal/** paths at the WebFilter level
     * (before Spring Cloud Gateway routing), so the filter intercepts
     * even requests that have no matching gateway route.
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebFilter internalApiProtectionFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            if (path.startsWith("/api/internal/")) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        };
    }
}
