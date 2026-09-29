package com.shms.service;

import com.shms.dto.request.LoginRequest;

import com.shms.dto.response.LoginResponse;


public interface AuthService {


    LoginResponse login(
            LoginRequest request
    );
}