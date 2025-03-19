package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task1;
    private Task task2;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Test Task 1");
        task1.setDescription("Test Description 1");
        task1.setStatus("TODO");
        task1.setDueDate(LocalDate.now().plusDays(7));
        task1.setPriority("HIGH");
        task1.setAssignedTo("john_doe");
        task1.setCreatedAt(LocalDateTime.now());
        task1.setUpdatedAt(LocalDateTime.now());

        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Test Task 2");
        task2.setDescription("Test Description 2");
        task2.setStatus("IN_PROGRESS");
        task2.setDueDate(LocalDate.now().plusDays(14));
        task2.setPriority("MEDIUM");
        task2.setAssignedTo("jane_doe");
        task2.setCreatedAt(LocalDateTime.now());
        task2.setUpdatedAt(LocalDateTime.now());

        taskRequest = new TaskRequest();
        taskRequest.setTitle("New Task");
        taskRequest.setDescription("New Description");
        taskRequest.setStatus("TODO");
        taskRequest.setDueDate(LocalDate.now().plusDays(5));
        taskRequest.setPriority("LOW");
        taskRequest.setAssignedTo("john_doe");
    }

    @Test
    void shouldGetAllTasks() {
        // Given
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // When
        List<Task> tasks = taskService.getAllTasks();

        // Then
        assertEquals(2, tasks.size());
        assertEquals("Test Task 1", tasks.get(0).getTitle());
        assertEquals("Test Task 2", tasks.get(1).getTitle());
        verify(taskRepository).findAll();
    }

    @Test
    void shouldGetTaskById() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        // When
        Task task = taskService.getTaskById(1L);

        // Then
        assertNotNull(task);
        assertEquals(1L, task.getId());
        assertEquals("Test Task 1", task.getTitle());
        verify(taskRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTaskById(999L);
        });
        verify(taskRepository).findById(999L);
    }

    @Test
    void shouldCreateTask() {
        // Given
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            savedTask.setId(3L);
            return savedTask;
        });

        // When
        Task createdTask = taskService.createTask(taskRequest);

        // Then
        assertNotNull(createdTask);
        assertEquals(3L, createdTask.getId());
        assertEquals("New Task", createdTask.getTitle());
        assertEquals("New Description", createdTask.getDescription());
        assertEquals("TODO", createdTask.getStatus());
        assertEquals("LOW", createdTask.getPriority());
        assertEquals("john_doe", createdTask.getAssignedTo());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldUpdateTask() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Task updatedTask = taskService.updateTask(1L, taskRequest);

        // Then
        assertNotNull(updatedTask);
        assertEquals(1L, updatedTask.getId());
        assertEquals("New Task", updatedTask.getTitle());
        assertEquals("New Description", updatedTask.getDescription());
        assertEquals("TODO", updatedTask.getStatus());
        assertEquals("LOW", updatedTask.getPriority());
        assertEquals("john_doe", updatedTask.getAssignedTo());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldToggleTaskCompletionFromInProgressToCompleted() {
        // Given
        Task inProgressTask = new Task();
        inProgressTask.setId(1L);
        inProgressTask.setStatus("IN_PROGRESS");
        
        when(taskRepository.findById(1L)).thenReturn(Optional.of(inProgressTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Task toggledTask = taskService.toggleTaskCompletion(1L);

        // Then
        assertNotNull(toggledTask);
        assertEquals("COMPLETED", toggledTask.getStatus());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldToggleTaskCompletionFromCompletedToInProgress() {
        // Given
        Task completedTask = new Task();
        completedTask.setId(1L);
        completedTask.setStatus("COMPLETED");
        
        when(taskRepository.findById(1L)).thenReturn(Optional.of(completedTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Task toggledTask = taskService.toggleTaskCompletion(1L);

        // Then
        assertNotNull(toggledTask);
        assertEquals("IN_PROGRESS", toggledTask.getStatus());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldDeleteTask() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepository).existsById(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTask() {
        // Given
        when(taskRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.deleteTask(999L);
        });
        verify(taskRepository).existsById(999L);
        verify(taskRepository, never()).deleteById(anyLong());
    }
} 