package com.taskmanager.application.service;

import com.taskmanager.application.dto.TaskRequest;
import com.taskmanager.application.dto.TaskResponse;
import com.taskmanager.application.dto.TaskSummaryResponse;
import com.taskmanager.application.mapper.TaskMapper;
import com.taskmanager.domain.exception.ResourceNotFoundException;
import com.taskmanager.domain.model.Task;
import com.taskmanager.domain.model.TaskStatus;
import com.taskmanager.domain.model.User;
import com.taskmanager.domain.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskService.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task testTask;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .name("John Doe")
            .email("john@test.com")
            .password("encodedPassword")
            .build();

        testTask = Task.builder()
            .id(1L)
            .title("Test Task")
            .description("Test Description")
            .status(TaskStatus.PENDING)
            .user(testUser)
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .build();

        taskRequest = new TaskRequest("New Task", "New Description", TaskStatus.PENDING);
    }

    @Test
    void createTask_Success() {
        // Arrange
        when(taskMapper.toResponse(any(Task.class))).thenReturn(
            new TaskResponse(1L, "Test Task", "Test Description", TaskStatus.PENDING, 
                1L, "John Doe", "john@test.com", LocalDateTime.now(), LocalDateTime.now())
        );
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskResponse response = taskService.createTask(taskRequest, testUser);

        // Assert
        assertNotNull(response);
        assertEquals("Test Task", response.title());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getTasks_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Task> taskPage = new PageImpl<>(List.of(testTask));
        when(taskRepository.findByUser(eq(testUser), any(Pageable.class))).thenReturn(taskPage);
        when(taskMapper.toSummary(any(Task.class))).thenReturn(
            new TaskSummaryResponse(1L, "Test Task", TaskStatus.PENDING, LocalDateTime.now(), LocalDateTime.now())
        );

        // Act
        Page<TaskSummaryResponse> response = taskService.getTasks(testUser, null, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(taskRepository).findByUser(eq(testUser), any(Pageable.class));
    }

    @Test
    void getTasks_WithStatusFilter_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Task> taskPage = new PageImpl<>(List.of(testTask));
        when(taskRepository.findByUserAndStatus(eq(testUser), eq(TaskStatus.PENDING), any(Pageable.class)))
            .thenReturn(taskPage);
        when(taskMapper.toSummary(any(Task.class))).thenReturn(
            new TaskSummaryResponse(1L, "Test Task", TaskStatus.PENDING, LocalDateTime.now(), LocalDateTime.now())
        );

        // Act
        Page<TaskSummaryResponse> response = taskService.getTasks(testUser, TaskStatus.PENDING, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(taskRepository).findByUserAndStatus(eq(testUser), eq(TaskStatus.PENDING), any(Pageable.class));
    }

    @Test
    void getTaskById_Success() {
        // Arrange
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testTask));
        when(taskMapper.toResponse(any(Task.class))).thenReturn(
            new TaskResponse(1L, "Test Task", "Test Description", TaskStatus.PENDING, 
                1L, "John Doe", "john@test.com", LocalDateTime.now(), LocalDateTime.now())
        );

        // Act
        TaskResponse response = taskService.getTaskById(1L, testUser);

        // Assert
        assertNotNull(response);
        assertEquals("Test Task", response.title());
    }

    @Test
    void getTaskById_NotFound_ThrowsException() {
        // Arrange
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(1L, testUser));
    }

    @Test
    void updateTask_Success() {
        // Arrange
        TaskRequest updateRequest = new TaskRequest("Updated Title", "Updated Description", TaskStatus.IN_PROGRESS);
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(
            new TaskResponse(1L, "Updated Title", "Updated Description", TaskStatus.IN_PROGRESS, 
                1L, "John Doe", "john@test.com", LocalDateTime.now(), LocalDateTime.now())
        );

        // Act
        TaskResponse response = taskService.updateTask(1L, updateRequest, testUser);

        // Assert
        assertNotNull(response);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTask_Success() {
        // Arrange
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testTask));
        doNothing().when(taskRepository).delete(testTask);

        // Act
        taskService.deleteTask(1L, testUser);

        // Assert
        verify(taskRepository).delete(testTask);
    }

    @Test
    void deleteTask_NotFound_ThrowsException() {
        // Arrange
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(1L, testUser));
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void updateTaskStatus_Success() {
        // Arrange
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(
            new TaskResponse(1L, "Test Task", "Test Description", TaskStatus.COMPLETED, 
                1L, "John Doe", "john@test.com", LocalDateTime.now(), LocalDateTime.now())
        );

        // Act
        TaskResponse response = taskService.updateTaskStatus(1L, TaskStatus.COMPLETED, testUser);

        // Assert
        assertNotNull(response);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_NullStatus_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.updateTaskStatus(1L, null, testUser));
    }
}

