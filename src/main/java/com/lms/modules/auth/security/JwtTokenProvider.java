package com.lms.modules.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtEncoder jwtEncoder;

    public String generateAccessToken(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if(userDetails == null){
            throw new IllegalArgumentException("User details can not be null!");
        }

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        Instant now = Instant.now();
        long validityInSeconds = 900; //15mins

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("lms-backend")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(validityInSeconds))
                .subject(userDetails.getUsername())
                .claim("authorities", authorities)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}
