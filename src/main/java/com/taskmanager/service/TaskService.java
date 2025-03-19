package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    @Transactional
    public Task createTask(TaskRequest taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setDueDate(taskRequest.getDueDate());
        task.setPriority(taskRequest.getPriority());
        task.setAssignedTo(taskRequest.getAssignedTo());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long id, TaskRequest taskRequest) {
        Task task = getTaskById(id);
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setDueDate(taskRequest.getDueDate());
        task.setPriority(taskRequest.getPriority());
        task.setAssignedTo(taskRequest.getAssignedTo());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Transactional
    public Task toggleTaskCompletion(Long id) {
        Task task = getTaskById(id);
        // Toggle between "COMPLETED" and "IN_PROGRESS"
        if ("COMPLETED".equals(task.getStatus())) {
            task.setStatus("IN_PROGRESS");
        } else {
            task.setStatus("COMPLETED");
        }
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
} 