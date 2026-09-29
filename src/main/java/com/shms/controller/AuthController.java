package com.shms.controller;

import com.shms.dto.request.LoginRequest;

import com.shms.dto.response.LoginResponse;

import com.shms.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Authentication APIs"
)
public class AuthController {


    private final AuthService authService;


    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description =
                    "Authenticate user and generate JWT token"
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful"
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid email or password"
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            )
    })
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {


        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}