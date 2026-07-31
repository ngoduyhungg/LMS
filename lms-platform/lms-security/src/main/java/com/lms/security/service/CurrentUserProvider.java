package com.lms.security.service;

import com.lms.security.principal.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Set;
import java.util.stream.Collectors;

public class CurrentUserProvider {

    public CurrentUser getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            return null;
        }

        Set<String> roles =
                jwtAuth.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());

        return new CurrentUser(

                jwtAuth.getToken().getSubject(),

                jwtAuth.getName(),

                jwtAuth.getToken().getClaimAsString("email"),

                roles

        );

    }

}