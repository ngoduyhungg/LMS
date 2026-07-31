package com.lms.security.config;

import com.lms.security.argumentresolver.CurrentUserArgumentResolver;
import com.lms.security.handler.CustomAccessDeniedHandler;
import com.lms.security.handler.CustomAuthenticationEntryPoint;
import com.lms.security.jwt.KeycloakJwtConverter;
import com.lms.security.service.CurrentUserProvider;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@AutoConfiguration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({DispatcherServlet.class, HttpSecurity.class})
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityAutoConfiguration {

    // =========================================================================
    // Security Infrastructure Beans
    // =========================================================================

    @Bean
    @ConditionalOnMissingBean
    public CurrentUserProvider currentUserProvider() {
        return new CurrentUserProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public KeycloakJwtConverter keycloakJwtConverter() {
        return new KeycloakJwtConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    @ConditionalOnMissingBean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }


    // =========================================================================
    // Spring MVC Argument Resolver Registration
    // =========================================================================


    @Bean
    @ConditionalOnMissingBean(CurrentUserArgumentResolver.class)
    public WebMvcConfigurer securityArgumentResolverConfigurer(
            CurrentUserProvider currentUserProvider) {

        return new WebMvcConfigurer() {
            @Override
            public void addArgumentResolvers(
                    @NonNull List<HandlerMethodArgumentResolver> resolvers) {
                resolvers.add(new CurrentUserArgumentResolver(currentUserProvider));
            }
        };
    }
}
