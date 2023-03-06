package com.pablito.basket.controller.fixtures;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class TokenFixtures {

    private final JwtEncoder jwtEncoder;

    public String generateToken(String username, String role) {

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(username)
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .claim("scope", role)//wartość string w którym są nazwy ról rozdzielone spacją
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
