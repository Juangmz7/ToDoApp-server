package com.juangomez.todoapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.juangomez.todoapp.model.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Body task must not be empty")
    private String body;

    private boolean completed;

    @NotNull(message = "Priority task must not be null")
    private TaskPriority priority;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate taskDate;

}
