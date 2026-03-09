package com.taskmanager.application.dto;

import com.taskmanager.domain.model.TaskStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for task summary in lists.
 */
public record TaskSummaryResponse(
    Long id,
    String title,
    TaskStatus status,
    LocalDateTime createdDate,
    LocalDateTime updatedDate
) {}

