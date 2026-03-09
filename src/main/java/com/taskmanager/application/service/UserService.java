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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user-related operations.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user.
     *
     * @param request the registration request
     * @return authentication response with tokens
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
            .name(request.name())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .role(Role.USER)
            .build();

        user = userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
            accessToken,
            refreshToken,
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getRole().name()
        );
    }

    /**
     * Authenticate a user.
     *
     * @param request the login request
     * @return authentication response with tokens
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
            accessToken,
            refreshToken,
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getRole().name()
        );
    }

    /**
     * Get user by email.
     *
     * @param email the user email
     * @return the user
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    /**
     * Get user by ID.
     *
     * @param id the user ID
     * @return the user
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /**
     * Refresh authentication tokens.
     *
     * @param refreshToken the refresh token
     * @return authentication response with new tokens
     */
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        User user = getUserByEmail(email);

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadRequestException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
            newAccessToken,
            newRefreshToken,
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getRole().name()
        );
    }
}

