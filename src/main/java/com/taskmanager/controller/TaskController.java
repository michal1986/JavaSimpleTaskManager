package com.taskmanager.controller;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable String status) {
        List<Task> allTasks = taskService.getAllTasks();
        List<Task> filteredTasks = allTasks.stream()
                .filter(task -> status.equalsIgnoreCase(task.getStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filteredTasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        return new ResponseEntity<>(taskService.createTask(taskRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.updateTask(id, taskRequest));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Task> toggleTaskCompletion(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.toggleTaskCompletion(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
} 