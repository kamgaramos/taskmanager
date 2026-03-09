package com.taskmanager.application.dto;

/**
 * Response DTO for authentication (login/register).
 */
public record AuthResponse(
    String accessToken,
    String refreshToken,
    Long userId,
    String email,
    String name,
    String role
) {}

