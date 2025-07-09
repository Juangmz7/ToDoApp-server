package com.juangomez.todoapp.serviceimpl;

import com.juangomez.todoapp.config.exception.task.InvalidTaskBodyException;
import com.juangomez.todoapp.config.exception.task.InvalidTaskPriorityException;
import com.juangomez.todoapp.config.exception.task.TaskNotFoundException;
import com.juangomez.todoapp.dto.TaskRequest;
import com.juangomez.todoapp.dto.TaskResponse;
import com.juangomez.todoapp.model.Task;
import com.juangomez.todoapp.model.User;
import com.juangomez.todoapp.model.UserPrincipal;
import com.juangomez.todoapp.model.enums.TaskPriority;
import com.juangomez.todoapp.repository.TaskRepository;
import com.juangomez.todoapp.repository.UserRepository;
import com.juangomez.todoapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    // Helper Task -> TaskResponse
    private TaskResponse entityToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getBody(),
                task.isCompleted(),
                task.getPriority(),
                task.getTaskDate()
        );
    }

    private List<TaskResponse> entityListToResponse(List<Task> tasks) {
        List<TaskResponse> taskResponses = new ArrayList<>();
        tasks.forEach(task -> taskResponses
                .add(entityToResponse(task))
        );
        return taskResponses;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Access denied: User not authenticated");
        }
        // Principal object must be an instance of UserPrincipal
        if (!(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new IllegalStateException("Config error, principal is not UserPrincipal");
        }
        return userPrincipal.getUsername();
    }

    @Override
    public TaskResponse getTaskByBody(String body) {
        String username = getCurrentUsername();
        // Fetch the task
        Task task = taskRepository.findByBodyAndUserUsername(body, username);
        return entityToResponse(task);
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        String username = getCurrentUsername();
        List<Task> tasks = taskRepository.findAllByUserUsername(username);
        // For each task a TaskRequest is created
        return entityListToResponse(tasks);
    }

    @Override
    public List<TaskResponse> getTasksByDate(LocalDate date) {
        String username = getCurrentUsername();
        List<Task> tasks = taskRepository.findAllByTaskDateAndUserUsername(date, username);
        return entityListToResponse(tasks);
    }

    @Override
    public List<TaskResponse> getTasksByPriority(TaskPriority priority) {  // Tirar exceptions
        String username = getCurrentUsername();
        List<Task> tasks = taskRepository.findAllByPriorityAndUserUsername(priority, username);
        return entityListToResponse(tasks);
    }

    @Override
    public List<TaskResponse> getCompletedTasks(boolean completed) {
        String username = getCurrentUsername();
        List<Task> tasks = taskRepository.findAllByIsCompletedAndUserUsername(completed, username);
        return entityListToResponse(tasks);
    }

    @Override
    public List<TaskResponse> getTasksOrderByPriorityAscending() {
        String username = getCurrentUsername();
        List<Task> tasks = taskRepository.findAllOrderByPriorityAsc(username);
        return entityListToResponse(tasks);
    }

    @Override
    public List<TaskResponse> getTasksOrderByPriorityDescending() {
        String username = getCurrentUsername();
        List<Task> tasks = taskRepository.findAllOrderByPriorityDesc(username);
        return entityListToResponse(tasks);
    }

    @Override
    public List<TaskResponse> getTasksByDateOrderByPriorityDescending(LocalDate date) {
        String username = getCurrentUsername();
        List<Task> tasks = taskRepository.findAllByTaskDateAndUserUsernameOrderByPriorityDesc(date, username);
        return entityListToResponse(tasks);
    }

    @Override
    public List<TaskResponse> getTasksByDateOrderByPriorityAscending(LocalDate date) {
        String username = getCurrentUsername();
        List<Task> tasks = taskRepository.findAllByTaskDateAndUserUsernameOrderByPriorityAsc(date, username);
        return entityListToResponse(tasks);
    }


    @Override
    public TaskResponse createTask(TaskRequest taskRequest) {
        String username = getCurrentUsername();
        // Fetch who creates the task
        User user = userRepository.findByUsername(username);
        // Entity object which is saved
        Task task = new Task();

        task.setUser(user);
        task.setBody(taskRequest.getBody());
        task.setTaskDate(taskRequest.getTaskDate());
        task.setPriority(taskRequest.getPriority());
        task.setCompleted(taskRequest.isCompleted());

        taskRepository.save(task);

        return entityToResponse(task);
    }

    @Override
    public TaskResponse updateTask(Integer id, TaskRequest taskRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(
                        "Task with id: " + id + " is not found"
                ));
        boolean changed = false;

        if (!task.getBody().equals(taskRequest.getBody())) {
            task.setBody(taskRequest.getBody());
            changed = true;
        }
        if (task.isCompleted() != taskRequest.isCompleted()) { // Para booleanos primitivos
            task.setCompleted(taskRequest.isCompleted());
            changed = true;
        }
        if (task.getPriority() != taskRequest.getPriority()) { // Para enums
            task.setPriority(taskRequest.getPriority());
            changed = true;
        }
        if (!task.getTaskDate().equals(taskRequest.getTaskDate())) { // Para LocalDate
            task.setTaskDate(taskRequest.getTaskDate());
            changed = true;
        }

        // Save the modification
        if (changed) {
            Task updatedTask = taskRepository.save(task);
            return entityToResponse(updatedTask);
        }

        // If there isn´t any changes returns actual task
        return entityToResponse(task);

    }

    @Override
    public boolean deleteTask(Integer id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(
                        "Task with id: " + id + " is not found"
                ));
        taskRepository.deleteById(id);
        return true;
    }
}
