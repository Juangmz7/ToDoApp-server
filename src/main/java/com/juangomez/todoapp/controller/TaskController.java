package com.juangomez.todoapp.controller;

import com.juangomez.todoapp.dto.TaskRequest;
import com.juangomez.todoapp.dto.TaskResponse;
import com.juangomez.todoapp.model.enums.TaskPriority;
import com.juangomez.todoapp.service.task.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/search")
    public ResponseEntity<TaskResponse> getTaskByBody(
            @NotBlank(message = "Body task must not be empty") @RequestParam String body
    ) {
        TaskResponse task = taskService.getTaskByBody(body);
        return ResponseEntity.ok(task);
    }


    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<TaskResponse>> getTasksByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy/mm/dd") LocalDate date
    ) {
        List<TaskResponse> tasks = taskService.getTasksByDate(date);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/by-priority")
    public ResponseEntity<List<TaskResponse>> getTasksByPriority(
            @NotNull @RequestParam TaskPriority priority
    ) {
        List<TaskResponse> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    // uncompleted -> completed = false
    @GetMapping("/completed")
    public ResponseEntity<List<TaskResponse>> getCompletedTasks(@RequestParam boolean completed) {
        List<TaskResponse> tasks = taskService.getCompletedTasks(completed);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/order-by-priority-asc")
    public ResponseEntity<List<TaskResponse>> getTasksOrderByPriorityAscending() {
        List<TaskResponse> tasks = taskService.getTasksOrderByPriorityAscending();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/order-by-priority-desc")
    public ResponseEntity<List<TaskResponse>> getTasksOrderByPriorityDescending() {
        List<TaskResponse> tasks = taskService.getTasksOrderByPriorityDescending();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/by-date-order-by-priority-asc")
    public ResponseEntity<List<TaskResponse>> getTasksByDateOrderByPriorityAscending(
            @NotNull @DateTimeFormat(pattern = "yyyy/MM/dd") @RequestParam LocalDate date
    ) {
        List<TaskResponse> tasks = taskService.getTasksByDateOrderByPriorityAscending(date);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/by-date-order-by-priority-desc")
    public ResponseEntity<List<TaskResponse>> getTasksByDateOrderByPriorityDescending(
            @NotNull @DateTimeFormat(pattern = "yyyy/MM/dd") @RequestParam LocalDate date
    ) {
        List<TaskResponse> tasks = taskService.getTasksByDateOrderByPriorityDescending(date);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest task) {
        TaskResponse taskCreated = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @Min(1) @PathVariable Integer id,
            @Valid @RequestBody TaskRequest taskDetails
    ) {
        TaskResponse taskCreated = taskService.updateTask(id, taskDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskCreated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteTask(@Min(1) @PathVariable int id) {
        Boolean taskCreated = taskService.deleteTask(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(taskCreated);
    }

    @GetMapping("/search-by-similarities")
    public ResponseEntity<List<TaskResponse>> getSimilarTasks(
            @NotBlank(message = "Body task must not be empty")@RequestParam String body
    ) {
//        System.out.println(">>> EmbeddingClient: " + embeddingClient.getClass());
        List<TaskResponse> task = taskService.getSimilarTasks(body);
        return ResponseEntity.ok(task);
    }
}
