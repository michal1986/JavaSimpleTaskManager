package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.exception.GlobalExceptionHandler;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.model.Task;
import com.taskmanager.security.JwtAuthenticationFilter;
import com.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;
    
    private Task task1;
    private Task task2;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
                
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
    @WithMockUser(roles = "USER")
    void shouldGetAllTasks() throws Exception {
        // Given
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskService.getAllTasks()).thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Task 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Test Task 2")));

        verify(taskService).getAllTasks();
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetTasksByStatus() throws Exception {
        // Given
        List<Task> tasks = Collections.singletonList(task1);
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        // When & Then
        mockMvc.perform(get("/api/tasks/status/TODO")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Task 1")))
                .andExpect(jsonPath("$[0].status", is("TODO")));

        verify(taskService).getAllTasks();
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetTaskById() throws Exception {
        // Given
        when(taskService.getTaskById(1L)).thenReturn(task1);

        // When & Then
        mockMvc.perform(get("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task 1")))
                .andExpect(jsonPath("$.status", is("TODO")));

        verify(taskService).getTaskById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn404WhenTaskNotFound() throws Exception {
        // Given
        when(taskService.getTaskById(999L)).thenThrow(new TaskNotFoundException("Task not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/tasks/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskService).getTaskById(999L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldCreateTask() throws Exception {
        // Given
        Task newTask = new Task();
        newTask.setId(3L);
        newTask.setTitle("New Task");
        newTask.setDescription("New Description");
        newTask.setStatus("TODO");
        newTask.setDueDate(taskRequest.getDueDate());
        newTask.setPriority("LOW");
        newTask.setAssignedTo("john_doe");
        newTask.setCreatedAt(LocalDateTime.now());
        newTask.setUpdatedAt(LocalDateTime.now());
        
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(newTask);

        // When & Then
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("New Task")))
                .andExpect(jsonPath("$.status", is("TODO")));

        verify(taskService).createTask(any(TaskRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldUpdateTask() throws Exception {
        // Given
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("New Task");
        updatedTask.setDescription("New Description");
        updatedTask.setStatus("TODO");
        updatedTask.setDueDate(taskRequest.getDueDate());
        updatedTask.setPriority("LOW");
        updatedTask.setAssignedTo("john_doe");
        updatedTask.setCreatedAt(LocalDateTime.now());
        updatedTask.setUpdatedAt(LocalDateTime.now());
        
        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(updatedTask);

        // When & Then
        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("New Task")))
                .andExpect(jsonPath("$.status", is("TODO")));

        verify(taskService).updateTask(eq(1L), any(TaskRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldToggleTaskCompletion() throws Exception {
        // Given
        Task toggledTask = new Task();
        toggledTask.setId(1L);
        toggledTask.setTitle("Test Task 1");
        toggledTask.setDescription("Test Description 1");
        toggledTask.setStatus("COMPLETED");
        toggledTask.setDueDate(LocalDate.now().plusDays(7));
        toggledTask.setPriority("HIGH");
        toggledTask.setAssignedTo("john_doe");
        toggledTask.setCreatedAt(LocalDateTime.now());
        toggledTask.setUpdatedAt(LocalDateTime.now());
        
        when(taskService.toggleTaskCompletion(1L)).thenReturn(toggledTask);

        // When & Then
        mockMvc.perform(patch("/api/tasks/1/toggle")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task 1")))
                .andExpect(jsonPath("$.status", is("COMPLETED")));

        verify(taskService).toggleTaskCompletion(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldDeleteTask() throws Exception {
        // Given
        doNothing().when(taskService).deleteTask(1L);

        // When & Then
        mockMvc.perform(delete("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn404WhenDeletingNonExistentTask() throws Exception {
        // Given
        doThrow(new TaskNotFoundException("Task not found with id: 999")).when(taskService).deleteTask(999L);

        // When & Then
        mockMvc.perform(delete("/api/tasks/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskService).deleteTask(999L);
    }

    @Test
    void shouldDenyAccessToUnauthenticatedUsers() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
                
        verifyNoInteractions(taskService);
    }
} 