package com.juangomez.todoapp.service;

import com.juangomez.todoapp.ai.service.TaskGenService;
import com.juangomez.todoapp.ai.service.VectorStoreService;
import com.juangomez.todoapp.config.exception.task.InvalidAudioFormatException;
import com.juangomez.todoapp.config.exception.task.NullDateException;
import com.juangomez.todoapp.config.exception.task.TaskNotFoundException;
import com.juangomez.todoapp.dto.TaskRequest;
import com.juangomez.todoapp.dto.TaskResponse;
import com.juangomez.todoapp.model.Task;
import com.juangomez.todoapp.model.User;
import com.juangomez.todoapp.model.UserPrincipal;
import com.juangomez.todoapp.model.enums.TaskPriority;
import com.juangomez.todoapp.repository.TaskRepository;
import com.juangomez.todoapp.repository.UserRepository;
import com.juangomez.todoapp.serviceimpl.task.TaskServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;
    @Mock private VectorStoreService vectorStoreService;
    @Mock private TaskGenService taskGenService;

    // Mocks for Security Context
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;
    @Mock private UserPrincipal userPrincipal;

    @InjectMocks
    private TaskServiceImpl taskService;

    private final String USERNAME = "juan";

    @BeforeEach
    void setUp() {
        // Mock Security Context to simulate authenticated user
        SecurityContextHolder.setContext(securityContext);

        // Lenient allows these stubs to be unused in some tests (like exception tests before auth check)
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getPrincipal()).thenReturn(userPrincipal);
        lenient().when(userPrincipal.getUsername()).thenReturn(USERNAME);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // --- Create Task Tests ---

    @Test
    void createTask_Success() {
        // Arrange
        TaskRequest request = new TaskRequest();
        request.setBody("New Task");
        request.setTaskDate(LocalDate.now());
        request.setPriority(TaskPriority.HIGH);
        request.setCompleted(false);

        User user = new User();
        user.setUsername(USERNAME);

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);

        // Mock save to return entity with ID
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> {
            Task t = i.getArgument(0);
            t.setId(1);
            return t;
        });

        // Act
        TaskResponse response = taskService.createTask(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("New Task", response.getBody());

        // Verify Vector Store was called
        verify(vectorStoreService).addVectorStore(anyList());
    }

    @Test
    void createTask_NullDate_ThrowsException() {
        TaskRequest request = new TaskRequest();
        request.setTaskDate(null); // This triggers the exception

        assertThrows(NullDateException.class, () -> taskService.createTask(request));

        verify(taskRepository, never()).save(any());
    }

    // --- Update Task Tests ---

    @Test
    void updateTask_Success() {
        // Arrange
        int taskId = 1;
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setBody("Old Body");
        existingTask.setTaskDate(LocalDate.now());
        existingTask.setPriority(TaskPriority.LOW);

        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setBody("Updated Body");
        updateRequest.setTaskDate(LocalDate.now().plusDays(1));
        updateRequest.setPriority(TaskPriority.HIGH);
        updateRequest.setCompleted(true);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        TaskResponse response = taskService.updateTask(taskId, updateRequest);

        // Assert
        assertEquals("Updated Body", response.getBody());
        assertEquals(TaskPriority.HIGH, response.getPriority());
        assertTrue(response.isCompleted());

        // Verify Vector Store Update
        verify(vectorStoreService).updateVectorStore(any(Task.class));
    }

    @Test
    void updateTask_NotFound_ThrowsException() {
        TaskRequest req = new TaskRequest();
        req.setTaskDate(LocalDate.now());

        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(99, req));
    }

    // --- Delete Task Tests ---

    @Test
    void deleteTask_Success() {
        int taskId = 1;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(new Task()));

        boolean result = taskService.deleteTask(taskId);

        assertTrue(result);
        verify(taskRepository).deleteById(taskId);
        verify(vectorStoreService).deleteVectorStore(taskId);
    }

    // --- AI / Audio Tests ---

    @Test
    void createTaskByAudio_Success() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", "audio.mp3", "audio/mpeg", "dummy content".getBytes());

        TaskRequest generatedRequest = new TaskRequest();
        generatedRequest.setBody("Audio Task");
        generatedRequest.setTaskDate(LocalDate.now());
        generatedRequest.setPriority(TaskPriority.MEDIUM);

        User user = new User();
        user.setUsername(USERNAME);

        when(taskGenService.taskRequestGenerator(file)).thenReturn(generatedRequest);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> {
            Task t = i.getArgument(0);
            t.setId(5);
            return t;
        });

        // Act
        TaskResponse response = taskService.createTaskByAudio(file);

        // Assert
        assertEquals("Audio Task", response.getBody());
        verify(taskGenService).taskRequestGenerator(file);
    }

    @Test
    void createTaskByAudio_InvalidFormat_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "text.txt", "text/plain", "dummy content".getBytes());

        assertThrows(InvalidAudioFormatException.class, () -> taskService.createTaskByAudio(file));
    }

    // --- Vector Search Tests ---

    @Test
    void getSimilarTasks_Success() {
        String query = "gym";
        List<Integer> ids = List.of(1, 2);
        Task t1 = new Task(); t1.setId(1); t1.setBody("Go to gym");
        Task t2 = new Task(); t2.setId(2); t2.setBody("Buy protein");

        when(vectorStoreService.similaritySearch(query, USERNAME)).thenReturn(ids);
        when(taskRepository.findAllById(ids)).thenReturn(List.of(t1, t2));

        List<TaskResponse> results = taskService.getSimilarTasks(query);

        assertEquals(2, results.size());
        verify(vectorStoreService).similaritySearch(query, USERNAME);
    }

    // --- Retrieve List Tests (Simple delegation check) ---

    @Test
    void getAllTasks() {
        when(taskRepository.findAllByUserUsername(USERNAME)).thenReturn(List.of(new Task()));

        List<TaskResponse> list = taskService.getAllTasks();

        assertFalse(list.isEmpty());
        verify(taskRepository).findAllByUserUsername(USERNAME);
    }
}