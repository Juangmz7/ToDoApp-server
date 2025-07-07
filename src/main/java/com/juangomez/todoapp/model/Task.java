package com.juangomez.todoapp.model;

import com.juangomez.todoapp.model.enums.TaskPriority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    private Integer id;
    private String body;
    private boolean isCompleted;
    private Date releaseDate;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @ManyToOne
    private User user;

}
