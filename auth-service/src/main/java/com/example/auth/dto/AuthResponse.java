package com.example.auth.dto;

public record AuthResponse(
        Long userId,
        String email,
        String role,
        String accessToken
) {}
