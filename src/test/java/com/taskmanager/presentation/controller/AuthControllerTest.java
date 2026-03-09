package com.taskmanager.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.application.dto.LoginRequest;
import com.taskmanager.application.dto.RegisterRequest;
import com.taskmanager.application.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for AuthController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void register_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest("John Doe", "john@test.com", "password123");
        when(userService.register(any(RegisterRequest.class))).thenReturn(
            new com.taskmanager.application.dto.AuthResponse(
                "accessToken", "refreshToken", 1L, "john@test.com", "John Doe", "USER"
            )
        );

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").value("accessToken"))
            .andExpect(jsonPath("$.email").value("john@test.com"));
    }

    @Test
    void register_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest("John Doe", "invalid-email", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_MissingFields_ReturnsBadRequest() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest("", "", "");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_ValidCredentials_ReturnsOk() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("john@test.com", "password123");
        when(userService.login(any(LoginRequest.class))).thenReturn(
            new com.taskmanager.application.dto.AuthResponse(
                "accessToken", "refreshToken", 1L, "john@test.com", "John Doe", "USER"
            )
        );

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("accessToken"));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("john@test.com", "wrongpassword");
        when(userService.login(any(LoginRequest.class)))
            .thenThrow(new com.taskmanager.domain.exception.BadRequestException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void refreshToken_ValidToken_ReturnsOk() throws Exception {
        // Arrange
        when(userService.refreshToken(anyString())).thenReturn(
            new com.taskmanager.application.dto.AuthResponse(
                "newAccessToken", "newRefreshToken", 1L, "john@test.com", "John Doe", "USER"
            )
        );

        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\": \"validToken\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("newAccessToken"));
    }

    @Test
    void logout_ReturnsOk() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/logout"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists());
    }
}

