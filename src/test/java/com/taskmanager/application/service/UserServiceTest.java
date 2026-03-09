package com.taskmanager.application.service;

import com.taskmanager.application.dto.AuthResponse;
import com.taskmanager.application.dto.LoginRequest;
import com.taskmanager.application.dto.RegisterRequest;
import com.taskmanager.domain.exception.BadRequestException;
import com.taskmanager.domain.exception.ResourceNotFoundException;
import com.taskmanager.domain.model.Role;
import com.taskmanager.domain.model.User;
import com.taskmanager.domain.repository.UserRepository;
import com.taskmanager.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .name("John Doe")
            .email("john@test.com")
            .password("encodedPassword")
            .role(Role.USER)
            .build();

        registerRequest = new RegisterRequest("John Doe", "john@test.com", "password123");
        loginRequest = new LoginRequest("john@test.com", "password123");
    }

    @Test
    void register_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        // Act
        AuthResponse response = userService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.accessToken());
        assertEquals("refreshToken", response.refreshToken());
        assertEquals("john@test.com", response.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        // Act
        AuthResponse response = userService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.accessToken());
        assertEquals("john@test.com", response.email());
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Arrange
        doThrow(new RuntimeException("Invalid credentials")).when(authenticationManager).authenticate(any());

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.login(loginRequest));
    }

    @Test
    void getUserByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Act
        User user = userService.getUserByEmail("john@test.com");

        // Assert
        assertNotNull(user);
        assertEquals("john@test.com", user.getEmail());
    }

    @Test
    void getUserByEmail_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("notfound@test.com"));
    }

    @Test
    void refreshToken_Success() {
        // Arrange
        when(jwtService.extractUsername(anyString())).thenReturn("john@test.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(anyString(), any(User.class))).thenReturn(true);
        when(jwtService.generateToken(any(User.class))).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("newRefreshToken");

        // Act
        AuthResponse response = userService.refreshToken("validRefreshToken");

        // Assert
        assertNotNull(response);
        assertEquals("newAccessToken", response.accessToken());
    }

    @Test
    void refreshToken_InvalidToken_ThrowsException() {
        // Arrange
        when(jwtService.extractUsername(anyString())).thenReturn("john@test.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(anyString(), any(User.class))).thenReturn(false);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.refreshToken("invalidToken"));
    }
}

