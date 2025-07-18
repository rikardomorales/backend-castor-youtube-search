package com.castor.youtube.security;

import com.castor.youtube.service.AuthRequest;
import com.castor.youtube.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        String requestPath = request.getServletPath();
        
        // Permitir acceso público a endpoints de autenticación y YouTube
        if (requestPath.startsWith("/api/auth/") || requestPath.startsWith("/api/youtube/")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Verificar si es una solicitud de login
        if (requestPath.equals("/api/auth/login")) {
            try {
                AuthRequest authRequest = new ObjectMapper().readValue(request.getInputStream(), AuthRequest.class);
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authRequest.getUsername(), authRequest.getPassword(), Collections.emptyList()
                        )
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String token = jwtUtil.generateToken(authentication.getName());
                response.addHeader("Authorization", "Bearer " + token);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid credentials: " + e.getMessage());
            }
        } else {
            // Validar token JWT para otras solicitudes
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                try {
                    String token = header.substring(7);
                    String username = jwtUtil.extractUsername(token);
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if (jwtUtil.validateToken(token, userDetails)) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                } catch (Exception e) {
                    // Log the exception but don't block the request for public endpoints
                    System.err.println("JWT validation error: " + e.getMessage());
                }
            }
            chain.doFilter(request, response);
        }
    }
}