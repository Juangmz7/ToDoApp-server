package com.juangomez.todoapp.repository;

import com.juangomez.todoapp.config.TestContainersConfig;
import com.juangomez.todoapp.model.Task;
import com.juangomez.todoapp.model.User;
import com.juangomez.todoapp.model.enums.TaskPriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfig.class)
class TaskRepositoryIntegrationTest {

    @Autowired private TestEntityManager entityManager;
    @Autowired private TaskRepository taskRepository;

    private User mainUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        mainUser = createUser("juan", "juan@test.com");
        otherUser = createUser("other", "other@test.com");
    }

    @Test
    void findByBodyAndUserUsername_ShouldReturnTask_WhenUserMatches() {
        // Arrange
        createTask("Buy Milk", mainUser, TaskPriority.HIGH, LocalDate.now(), false);
        createTask("Secret Task", otherUser, TaskPriority.HIGH, LocalDate.now(), false); // Noise

        // Act
        Task result = taskRepository.findByBodyAndUserUsername("Buy Milk", "juan");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo("juan");
    }

    @Test
    void findAllOrderByPriorityDesc_ShouldOrderCorrectly_HighToLow() {
        // Arrange
        createTask("Low Priority", mainUser, TaskPriority.LOW, LocalDate.now(), false);
        createTask("High Priority", mainUser, TaskPriority.HIGH, LocalDate.now(), false);
        createTask("Medium Priority", mainUser, TaskPriority.MEDIUM, LocalDate.now(), false);

        // Act
        List<Task> result = taskRepository.findAllOrderByPriorityDesc("juan");

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(result.get(1).getPriority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(result.get(2).getPriority()).isEqualTo(TaskPriority.LOW);
    }

    @Test
    void findAllByIsCompletedAndUserUsername_ShouldFilterStatus() {
        // Arrange
        createTask("Pending", mainUser, TaskPriority.MEDIUM, LocalDate.now(), false);
        createTask("Done", mainUser, TaskPriority.MEDIUM, LocalDate.now(), true);

        // Act
        List<Task> result = taskRepository.findAllByIsCompletedAndUserUsername(true, "juan");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getBody()).isEqualTo("Done");
    }

    // --- Helpers ---

    private User createUser(String username, String email) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword("encodedPass"); // Mock value
        return entityManager.persistFlushFind(u);
    }

    private void createTask(String body, User user, TaskPriority priority, LocalDate date, boolean completed) {
        Task t = new Task();
        t.setBody(body);
        t.setUser(user);
        t.setPriority(priority);
        t.setTaskDate(date);
        t.setCompleted(completed);

        entityManager.persist(t);
        entityManager.flush();
    }
}