package com.juangomez.todoapp.repository;

import com.juangomez.todoapp.model.Task;
import com.juangomez.todoapp.model.enums.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    Task findByBodyAndUserUsername(String body, String userUsername);

    List<Task> findAllByUserUsername(String userUsername);

    List<Task> findAllByTaskDateAndUserUsername(LocalDate taskDate, String userUsername);

    List<Task> findAllByPriorityAndUserUsername(TaskPriority priority, String userUsername);

    List<Task> findAllByIsCompletedAndUserUsername(boolean completed, String userUsername);

    List<Task> findAllByTaskDateAndUserUsernameOrderByPriorityDesc(LocalDate taskDate, String username);

    List<Task> findAllByTaskDateAndUserUsernameOrderByPriorityAsc(LocalDate date, String username);

    @Query("" +
            "SELECT t " +
            "FROM Task t " +
            "WHERE t.user.username= :username " +
            "ORDER BY t.priority ASC"
    )
    List<Task> findAllOrderByPriorityAsc(@Param("username") String username);

    @Query("" +
            "SELECT t " +
            "FROM Task t " +
            "WHERE t.user.username= :username " +
            "ORDER BY t.priority DESC"
    )
    List<Task> findAllOrderByPriorityDesc(@Param("username") String username);

}
