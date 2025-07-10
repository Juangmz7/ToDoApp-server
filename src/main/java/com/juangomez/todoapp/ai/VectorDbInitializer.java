package com.juangomez.todoapp.ai;

import com.juangomez.todoapp.ai.service.VectorStoreService;
import com.juangomez.todoapp.model.Task;
import com.juangomez.todoapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  Executes when db is already loaded
 */
@Component
public class VectorDbInitializer implements ApplicationRunner {

    @Autowired
    private VectorStoreService vectorStoreService;

    @Autowired
    private TaskRepository taskRepository;

    // Fetch all data from db
    @Override
    public void run(ApplicationArguments args) {
        List<Task> tasks = taskRepository.findAll();
        if (tasks.isEmpty())
            return;
        vectorStoreService.addVectorStore(tasks);
    }
}

