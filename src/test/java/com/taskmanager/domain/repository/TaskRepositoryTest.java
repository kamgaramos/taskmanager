package com.taskmanager.domain.repository;

import com.taskmanager.domain.model.Task;
import com.taskmanager.domain.model.TaskStatus;
import com.taskmanager.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TaskRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .name("John Doe")
            .email("john@test.com")
            .password("password")
            .build();
        testUser = userRepository.save(testUser);

        testTask = Task.builder()
            .title("Test Task")
            .description("Test Description")
            .status(TaskStatus.PENDING)
            .user(testUser)
            .build();
        testTask = taskRepository.save(testTask);
    }

    @Test
    void findByUser_ReturnsUserTasks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<Task> tasks = taskRepository.findByUser(testUser, pageable);

        // Assert
        assertEquals(1, tasks.getTotalElements());
        assertEquals("Test Task", tasks.getContent().get(0).getTitle());
    }

    @Test
    void findByUserAndStatus_ReturnsFilteredTasks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<Task> tasks = taskRepository.findByUserAndStatus(testUser, TaskStatus.PENDING, pageable);

        // Assert
        assertEquals(1, tasks.getTotalElements());
        assertEquals(TaskStatus.PENDING, tasks.getContent().get(0).getStatus());
    }

    @Test
    void findByUserAndStatus_WrongStatus_ReturnsEmpty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<Task> tasks = taskRepository.findByUserAndStatus(testUser, TaskStatus.COMPLETED, pageable);

        // Assert
        assertEquals(0, tasks.getTotalElements());
    }

    @Test
    void findByIdAndUser_WhenTaskBelongsToUser_ReturnsTask() {
        // Act
        Optional<Task> found = taskRepository.findByIdAndUser(testTask.getId(), testUser);

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Test Task", found.get().getTitle());
    }

    @Test
    void findByIdAndUser_WhenTaskDoesNotBelongToUser_ReturnsEmpty() {
        // Arrange
        User otherUser = User.builder()
            .name("Other User")
            .email("other@test.com")
            .password("password")
            .build();
        otherUser = userRepository.save(otherUser);

        // Act
        Optional<Task> found = taskRepository.findByIdAndUser(testTask.getId(), otherUser);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void save_NewTask_PersistsSuccessfully() {
        // Arrange
        Task newTask = Task.builder()
            .title("New Task")
            .description("New Description")
            .status(TaskStatus.IN_PROGRESS)
            .user(testUser)
            .build();

        // Act
        Task saved = taskRepository.save(newTask);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("New Task", saved.getTitle());
    }

    @Test
    void delete_RemovesTask() {
        // Act
        taskRepository.delete(testTask);

        // Assert
        Optional<Task> found = taskRepository.findById(testTask.getId());
        assertFalse(found.isPresent());
    }
}

