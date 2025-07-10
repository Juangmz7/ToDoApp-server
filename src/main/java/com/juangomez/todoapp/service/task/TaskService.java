package com.juangomez.todoapp.service.task;

import com.juangomez.todoapp.dto.TaskRequest;
import com.juangomez.todoapp.dto.TaskResponse;
import com.juangomez.todoapp.model.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface TaskService {

    TaskResponse getTaskByBody(String body);

    List<TaskResponse> getAllTasks();

    List<TaskResponse> getTasksByDate(LocalDate date);

    List<TaskResponse> getTasksByPriority(TaskPriority priority);

    List<TaskResponse> getCompletedTasks(boolean completed);

    List<TaskResponse> getTasksOrderByPriorityAscending();

    List<TaskResponse> getTasksOrderByPriorityDescending();

    List<TaskResponse> getTasksByDateOrderByPriorityAscending(LocalDate date);

    List<TaskResponse> getTasksByDateOrderByPriorityDescending(LocalDate date);

    TaskResponse createTask(TaskRequest taskRequest);

    TaskResponse updateTask(Integer id, TaskRequest taskRequest);

    boolean deleteTask(Integer id);

    List<TaskResponse> getSimilarTasks(String body);
}