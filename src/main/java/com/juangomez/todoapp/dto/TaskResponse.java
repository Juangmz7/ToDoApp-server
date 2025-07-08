package com.juangomez.todoapp.dto;

import com.juangomez.todoapp.model.enums.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    private Integer id;
    private String body;
    private boolean isCompleted;
    private TaskPriority priority;
    private LocalDate taskDate;
}