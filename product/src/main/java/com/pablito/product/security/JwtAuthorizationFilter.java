package com.pablito.product.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;


public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }


    //Sprawdzanie czy request który jest przetwarzany jest z użytkownikiem czy bez. Sprawdza z jakim użytkownikiem.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        var token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        var decodedJWT = JWT.require(Algorithm.HMAC512("masakracja"))
                .build()
                .verify(token.split(" ")[1]);

        var email = decodedJWT.getSubject();

        if (email == null) {
            response.setStatus(401); //unauthorized //403 forbidden, access denied
            return;
        }

        var authorities = decodedJWT.getClaims().get("authorities").as(String.class); //role połączone po przecinku

        var grantedAuthorities = new ArrayList<SimpleGrantedAuthority>();

        if (authorities != null && !authorities.isEmpty()) {
            grantedAuthorities = Arrays.stream(authorities.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        //obiekt do przechowywania w kontekscie security
        var usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(email, null, grantedAuthorities);

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        chain.doFilter(request, response);
    }
}
