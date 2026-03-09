package com.taskmanager.application.mapper;

import com.taskmanager.application.dto.TaskResponse;
import com.taskmanager.application.dto.TaskSummaryResponse;
import com.taskmanager.domain.model.Task;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for converting Task entity to DTOs.
 */
@Component
public class TaskMapper {

    /**
     * Convert Task entity to TaskResponse.
     *
     * @param task the task entity
     * @return the task response
     */
    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getUser().getId(),
            task.getUser().getName(),
            task.getUser().getEmail(),
            task.getCreatedDate(),
            task.getUpdatedDate()
        );
    }

    /**
     * Convert Task entity to TaskSummaryResponse.
     *
     * @param task the task entity
     * @return the task summary response
     */
    public TaskSummaryResponse toSummary(Task task) {
        return new TaskSummaryResponse(
            task.getId(),
            task.getTitle(),
            task.getStatus(),
            task.getCreatedDate(),
            task.getUpdatedDate()
        );
    }

    /**
     * Convert list of tasks to list of task summary responses.
     *
     * @param tasks the list of tasks
     * @return list of task summary responses
     */
    public List<TaskSummaryResponse> toSummaryList(List<Task> tasks) {
        return tasks.stream()
            .map(this::toSummary)
            .toList();
    }
}

