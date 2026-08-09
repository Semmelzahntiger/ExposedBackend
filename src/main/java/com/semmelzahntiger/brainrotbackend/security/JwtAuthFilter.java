package com.semmelzahntiger.brainrotbackend.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.semmelzahntiger.brainrotbackend.data.AppUser;
import com.semmelzahntiger.brainrotbackend.data.UserPrincipal;
import com.semmelzahntiger.brainrotbackend.service.JWTService;
import com.semmelzahntiger.brainrotbackend.service.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    public JwtAuthFilter(final JWTService jwtService) {
        this.jwtService = jwtService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String token = header.replace("Bearer ", "");
        Optional<DecodedJWT> decoded = jwtService.getDecodedJWT(token);
        if(decoded.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        DecodedJWT jwt = decoded.get();
        UUID userUUID = UUID.fromString(jwt.getSubject());
        String userMail = jwt.getClaim("email").asString();
        String username = String.valueOf(jwt.getClaim("username"));
        String[] authorities = jwt.getClaim("authorities").asArray(String.class);
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + authority));
        }
        UserPrincipal userPrincipal = new UserPrincipal(userUUID, userMail, username, authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, null, grantedAuthorities));
        filterChain.doFilter(request, response);
    }
}
