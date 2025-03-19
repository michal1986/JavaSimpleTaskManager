package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Status is required")
    private String status;

    private LocalDate dueDate;

    private String priority;

    private String assignedTo;
} 