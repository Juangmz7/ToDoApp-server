package com.juangomez.todoapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.juangomez.todoapp.model.enums.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    private String body;
    private boolean isCompleted;
    private TaskPriority priority;

    @JsonFormat(pattern = "yyyy/mm/dd")
    private LocalDate taskDate;
}
