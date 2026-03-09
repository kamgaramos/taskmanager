package com.taskmanager.domain.repository;

import com.taskmanager.domain.model.Task;
import com.taskmanager.domain.model.TaskStatus;
import com.taskmanager.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Task entity operations.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks for a user with pagination.
     *
     * @param user   the user who owns the tasks
     * @param page   the pagination information
     * @return page of tasks
     */
    Page<Task> findByUser(User user, Pageable page);

    /**
     * Find tasks by user and status with pagination.
     *
     * @param user   the user who owns the tasks
     * @param status the task status to filter by
     * @param page   the pagination information
     * @return page of tasks
     */
    Page<Task> findByUserAndStatus(User user, TaskStatus status, Pageable page);

    /**
     * Find task by id and user.
     *
     * @param id   the task id
     * @param user the user who owns the task
     * @return optional containing the task if found
     */
    Optional<Task> findByIdAndUser(Long id, User user);

    /**
     * Check if task exists by id and user.
     *
     * @param id   the task id
     * @param user the user who owns the task
     * @return true if task exists
     */
    boolean existsByIdAndUser(Long id, User user);

    /**
     * Find all tasks with eager user loading.
     *
     * @param pageable the pagination information
     * @return page of tasks with user eagerly loaded
     */
    @Query("SELECT t FROM Task t JOIN FETCH t.user")
    Page<Task> findAllWithUser(Pageable pageable);
}

