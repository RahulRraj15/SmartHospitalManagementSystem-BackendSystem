package com.shms.security.filter;

import com.shms.security.jwt.JwtService;

import com.shms.security.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {


    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {


        // --------------------------------------------------------
        // GET AUTHORIZATION HEADER
        // --------------------------------------------------------

        final String authHeader =
                request.getHeader("Authorization");


        // --------------------------------------------------------
        // CHECK BEARER TOKEN
        // --------------------------------------------------------

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        // --------------------------------------------------------
        // EXTRACT JWT
        // --------------------------------------------------------

        final String jwt =
                authHeader.substring(7);


        final String userEmail;


        try {

            userEmail =
                    jwtService.extractUsername(jwt);

        } catch (Exception ex) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        // --------------------------------------------------------
        // AUTHENTICATE USER
        // --------------------------------------------------------

        if (userEmail != null &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {


            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(
                                    userEmail
                            );


            // ----------------------------------------------------
            // VALIDATE JWT
            // ----------------------------------------------------

            if (jwtService.isTokenValid(
                    jwt,
                    userDetails)) {


                UsernamePasswordAuthenticationToken
                        authentication =

                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );


                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );


                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );
            }
        }


        // --------------------------------------------------------
        // CONTINUE FILTER CHAIN
        // --------------------------------------------------------

        filterChain.doFilter(
                request,
                response
        );
    }
}