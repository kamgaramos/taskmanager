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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for task-related operations.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    /**
     * Create a new task.
     *
     * @param request the task request
     * @param user    the owner of the task
     * @return the created task response
     */
    @Transactional
    public TaskResponse createTask(TaskRequest request, User user) {
        TaskStatus status = request.status() != null ? request.status() : TaskStatus.PENDING;

        Task task = Task.builder()
            .title(request.title())
            .description(request.description())
            .status(status)
            .user(user)
            .build();

        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    /**
     * Get all tasks for a user with optional status filter.
     *
     * @param user     the owner of the tasks
     * @param status   optional status filter
     * @param pageable pagination information
     * @return page of task summary responses
     */
    @Transactional(readOnly = true)
    public Page<TaskSummaryResponse> getTasks(User user, TaskStatus status, Pageable pageable) {
        Page<Task> tasks;
        if (status != null) {
            tasks = taskRepository.findByUserAndStatus(user, status, pageable);
        } else {
            tasks = taskRepository.findByUser(user, pageable);
        }
        return tasks.map(taskMapper::toSummary);
    }

    /**
     * Get a task by ID for a specific user.
     *
     * @param id   the task ID
     * @param user the owner of the task
     * @return the task response
     */
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        return taskMapper.toResponse(task);
    }

    /**
     * Update a task.
     *
     * @param id      the task ID
     * @param request the task request
     * @param user    the owner of the task
     * @return the updated task response
     */
    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        if (request.title() != null && !request.title().isBlank()) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }

        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    /**
     * Delete a task.
     *
     * @param id   the task ID
     * @param user the owner of the task
     */
    @Transactional
    public void deleteTask(Long id, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        taskRepository.delete(task);
    }

    /**
     * Update task status.
     *
     * @param id     the task ID
     * @param status the new status
     * @param user   the owner of the task
     * @return the updated task response
     */
    @Transactional
    public TaskResponse updateTaskStatus(Long id, TaskStatus status, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        task.setStatus(status);
        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }
}

