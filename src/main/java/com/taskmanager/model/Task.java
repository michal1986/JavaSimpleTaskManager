package com.taskmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @NotBlank(message = "Status is required")
    @Column(name = "status")
    private String status;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "priority")
    private String priority;

    @Column(name = "assigned_to")
    private String assignedTo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 