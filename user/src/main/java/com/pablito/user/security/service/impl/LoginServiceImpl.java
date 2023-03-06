package com.pablito.user.security.service.impl;

import com.pablito.user.domain.dto.LoginDto;
import com.pablito.user.domain.dto.TokenDto;
import com.pablito.user.security.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;

    private final JwtEncoder jwtEncoder;

    @Override
    public TokenDto login(LoginDto loginDto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getLogin(), loginDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .claim("scope", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(" ")))//wartość string w którym są nazwy ról rozdzielone spacją
                .build();

        return new TokenDto(jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());
        /*
        JJWT IMPLEMENTATION
         */
//        Claims claims = new DefaultClaims()
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
//                .setSubject(authentication.getName());
//
//        claims.put("authorities", authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(",")));
//
//        String token = Jwts.builder()
//                .setClaims(claims)
//                .signWith(SignatureAlgorithm.HS512, "masakracja")
//                .compact();


        /*
        JAVA JWT
         */
//        String token = JWT.create()
//                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
//                .withSubject(authentication.getName())
//                .withClaim("authorities", authentication.getAuthorities().stream()
//                        .map(GrantedAuthority::getAuthority)
//                        .collect(Collectors.joining(",")))
//                .sign(Algorithm.HMAC512("masakracja"));

/*        return null;*/
    }
}
