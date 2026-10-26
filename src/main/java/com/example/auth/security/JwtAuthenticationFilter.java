package com.example.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collections;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private CustomerUserDetailService customerUserDetailService;


    public JwtAuthenticationFilter(JwtService jwtService , CustomerUserDetailService customerUserDetailService) {
        this.jwtService = jwtService;
        this.customerUserDetailService = customerUserDetailService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
        String authHeader = request.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token =  authHeader.substring(7);

            String subject = jwtService.extractSubject(token);

           Authentication auth =  SecurityContextHolder.getContext().getAuthentication();

           if(auth == null){
               UserDetails userDetails = customerUserDetailService.loadUserByUsername(subject);
               UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                       subject , null , userDetails.getAuthorities()
               );
               SecurityContextHolder.getContext()
                       .setAuthentication(usernamePasswordAuthenticationToken);
           }

        }
        }catch (UsernameNotFoundException ex) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }catch(io.jsonwebtoken.JwtException | IllegalArgumentException ex){
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Invalid or expired token\"}"
            );
            return;
            }

        filterChain.doFilter(request,response);

    }
}
