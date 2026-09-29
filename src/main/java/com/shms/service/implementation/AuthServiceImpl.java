package com.shms.service.implementation;

import com.shms.dto.request.LoginRequest;
import com.shms.dto.response.LoginResponse;
import com.shms.service.AuthService;

import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public LoginResponse login(LoginRequest request) {

        throw new UnsupportedOperationException(
                "Authentication is temporarily disabled"
        );
    }
}





























/*
 package com.shms.service.implementation;
 

import com.shms.dto.request.LoginRequest;
import com.shms.dto.response.LoginResponse;

import com.shms.security.jwt.JwtService;

import com.shms.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl
        implements AuthService {


    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;


    @Override
    public LoginResponse login(
            LoginRequest request) {


        // --------------------------------------------------------
        // STEP 1: AUTHENTICATE USER
        // --------------------------------------------------------

        Authentication authentication =

                authenticationManager.authenticate(

                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );


        // --------------------------------------------------------
        // STEP 2: GET AUTHENTICATED USER
        // --------------------------------------------------------

        UserDetails userDetails =
                (UserDetails)
                        authentication.getPrincipal();


        // --------------------------------------------------------
        // STEP 3: GENERATE JWT
        // --------------------------------------------------------

        String token =
                jwtService.generateToken(
                        userDetails
                );


        // --------------------------------------------------------
        // STEP 4: RETURN RESPONSE
        // --------------------------------------------------------

        return new LoginResponse(

                token,

                "Bearer",

                86400000
        );
    }
}
*/
