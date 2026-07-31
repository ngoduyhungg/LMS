package com.lms.security.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts Keycloak JWT token into Spring Security Authentication.
 *
 * Keycloak embeds roles in: jwt.realm_access.roles
 * This converter extracts them and maps to "ROLE_INSTRUCTOR", "ROLE_STUDENT", etc.
 *
 * Usage in @PreAuthorize: hasRole('INSTRUCTOR')  → Spring checks "ROLE_INSTRUCTOR"
 */
@Component
public class KeycloakJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    // Default converter handles scope-based authorities (optional)
    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Merge default scope authorities + Keycloak realm roles
        Collection<GrantedAuthority> authorities = Stream.concat(
            defaultConverter.convert(jwt).stream(),
            extractRealmRoles(jwt).stream()
        ).collect(Collectors.toSet());

        // Use email as principal name (or "sub" if preferred)
        String principalName = jwt.getClaimAsString("email") != null
            ? jwt.getClaimAsString("email")
            : jwt.getSubject();

        return new JwtAuthenticationToken(jwt, authorities, principalName);
    }

    /**
     * Extracts roles from Keycloak's realm_access claim.
     *
     * JWT structure:
     * {
     *   "realm_access": {
     *     "roles": ["INSTRUCTOR", "ADMIN", "offline_access", "uma_authorization"]
     *   }
     * }
     */
    @SuppressWarnings("unchecked")
    private Set<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return Set.of();
        }

        List<String> roles = (List<String>) realmAccess.get("roles");

        return roles.stream()
            // Filter out Keycloak's internal roles
            .filter(role -> !role.equals("offline_access") && !role.equals("uma_authorization"))
            // Spring Security convention: prefix with "ROLE_"
            // @PreAuthorize("hasRole('INSTRUCTOR')") checks for "ROLE_INSTRUCTOR"
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            .collect(Collectors.toSet());
    }
}