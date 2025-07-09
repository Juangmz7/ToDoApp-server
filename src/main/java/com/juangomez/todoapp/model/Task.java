package com.juangomez.todoapp.model;

import com.juangomez.todoapp.model.enums.TaskPriority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String body;
    private boolean isCompleted;
    private LocalDate taskDate;

    @Enumerated(EnumType.ORDINAL)
    private TaskPriority priority;

    @ManyToOne
    private User user;

}
