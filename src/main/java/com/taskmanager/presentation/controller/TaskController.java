package com.taskmanager.presentation.controller;

import com.taskmanager.application.dto.TaskRequest;
import com.taskmanager.application.dto.TaskResponse;
import com.taskmanager.application.dto.TaskSummaryResponse;
import com.taskmanager.application.service.TaskService;
import com.taskmanager.domain.model.TaskStatus;
import com.taskmanager.domain.model.User;
import com.taskmanager.infrastructure.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for task management endpoints.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task Management APIs")
public class TaskController {

    private final TaskService taskService;

    /**
     * Get all tasks for the authenticated user.
     */
    @Operation(summary = "Get all tasks", description = "Get paginated list of tasks for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<Page<TaskSummaryResponse>> getTasks(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) TaskStatus status,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        User user = userDetails.getUser();
        Page<TaskSummaryResponse> tasks = taskService.getTasks(user, status, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get a task by ID.
     */
    @Operation(summary = "Get task by ID", description = "Get detailed task information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        User user = userDetails.getUser();
        TaskResponse task = taskService.getTaskById(id, user);
        return ResponseEntity.ok(task);
    }

    /**
     * Create a new task.
     */
    @Operation(summary = "Create task", description = "Create a new task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TaskRequest request
    ) {
        User user = userDetails.getUser();
        TaskResponse task = taskService.createTask(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    /**
     * Update a task.
     */
    @Operation(summary = "Update task", description = "Update an existing task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request
    ) {
        User user = userDetails.getUser();
        TaskResponse task = taskService.updateTask(id, request, user);
        return ResponseEntity.ok(task);
    }

    /**
     * Delete a task.
     */
    @Operation(summary = "Delete task", description = "Delete a task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        User user = userDetails.getUser();
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update task status.
     */
    @Operation(summary = "Update task status", description = "Update the status of a task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestBody TaskStatus status
    ) {
        User user = userDetails.getUser();
        TaskResponse task = taskService.updateTaskStatus(id, status, user);
        return ResponseEntity.ok(task);
    }
}

