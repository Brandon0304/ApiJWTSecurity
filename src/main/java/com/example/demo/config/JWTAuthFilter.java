package com.example.demo.config;


import com.example.demo.service.JWTUtils;
import com.example.demo.service.OurUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private OurUserDetailsService ourUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        System.out.println("Auth Header recibido: " + authHeader);
        System.out.println("URL de la solicitud: " + request.getRequestURI());
        
        final String jwtToken;
        final String userEmail;
        
        if(authHeader == null || authHeader.isBlank()) {
            System.out.println("No se encontró header de autorización");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtToken = authHeader.substring(7);
            System.out.println("Token extraído: " + jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...");
            userEmail = jwtUtils.extractUsername(jwtToken);
            System.out.println("Email extraído del token: " + userEmail);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = ourUserDetailsService.loadUserByUsername(userEmail);
                System.out.println("Roles del usuario: " + userDetails.getAuthorities());
                
                if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
                    System.out.println("Token validado correctamente");
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(token);
                    SecurityContextHolder.setContext(securityContext);
                    System.out.println("Autenticación establecida en el contexto de seguridad");
                } else {
                    System.out.println("Token inválido o expirado");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al procesar el token: " + e.getMessage());
            e.printStackTrace();
        }
        
        filterChain.doFilter(request, response);
    }
}
