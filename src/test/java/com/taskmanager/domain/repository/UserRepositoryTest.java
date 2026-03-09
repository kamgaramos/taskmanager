package com.taskmanager.domain.repository;

import com.taskmanager.domain.model.Role;
import com.taskmanager.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail_WhenEmailExists_ReturnsTrue() {
        // Arrange
        User user = User.builder()
            .name("John Doe")
            .email("john@test.com")
            .password("password")
            .role(Role.USER)
            .build();
        userRepository.save(user);

        // Act
        boolean exists = userRepository.existsByEmail("john@test.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByEmail_WhenEmailDoesNotExist_ReturnsFalse() {
        // Act
        boolean exists = userRepository.existsByEmail("notfound@test.com");

        // Assert
        assertFalse(exists);
    }

    @Test
    void findByEmail_WhenEmailExists_ReturnsUser() {
        // Arrange
        User user = User.builder()
            .name("John Doe")
            .email("john@test.com")
            .password("password")
            .role(Role.USER)
            .build();
        userRepository.save(user);

        // Act
        Optional<User> found = userRepository.findByEmail("john@test.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("john@test.com", found.get().getEmail());
    }

    @Test
    void findByEmail_WhenEmailDoesNotExist_ReturnsEmpty() {
        // Act
        Optional<User> found = userRepository.findByEmail("notfound@test.com");

        // Assert
        assertFalse(found.isPresent());
    }
}

