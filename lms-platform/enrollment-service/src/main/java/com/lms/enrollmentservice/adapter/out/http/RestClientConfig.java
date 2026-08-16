package com.lms.enrollmentservice.adapter.out.http;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class RestClientConfig {

    @Value("${course-service.url:http://localhost:8081}")
    private String courseServiceUrl;

    @Value("${user-service.url:http://localhost:8083}")
    private String userServiceUrl;

    @Bean
    public ClientHttpRequestInterceptor tokenRelayInterceptor() {
        return (request, body, execution) -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest servletRequest = attributes.getRequest();
                String authHeader = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
                if (authHeader != null) {
                    request.getHeaders().add(HttpHeaders.AUTHORIZATION, authHeader);
                }
            }
            return execution.execute(request, body);
        };
    }

    @Bean
    public RestClient courseServiceClient() {
        return RestClient.builder()
                .baseUrl(courseServiceUrl)
                .requestInterceptor(tokenRelayInterceptor())
                .build();
    }

    @Bean
    public RestClient userServiceClient(ClientHttpRequestInterceptor tokenRelayInterceptor) {
        return RestClient.builder()
                .baseUrl(userServiceUrl)
                .requestInterceptor(tokenRelayInterceptor)
                .build();
    }
}