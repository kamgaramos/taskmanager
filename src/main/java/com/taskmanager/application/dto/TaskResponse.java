package com.taskmanager.application.dto;

import com.taskmanager.domain.model.TaskStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for detailed task information.
 */
public record TaskResponse(
    Long id,
    String title,
    String description,
    TaskStatus status,
    Long userId,
    String userName,
    String userEmail,
    LocalDateTime createdDate,
    LocalDateTime updatedDate
) {}

