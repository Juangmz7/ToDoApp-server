package com.juangomez.todoapp.ai.serviceimpl;

import com.juangomez.todoapp.config.exception.task.InvalidTaskBodyException;
import com.juangomez.todoapp.ai.service.VectorStoreService;
import com.juangomez.todoapp.config.exception.user.InvalidUsernameException;
import com.juangomez.todoapp.model.Task;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TaskVectorStoreServiceImpl implements VectorStoreService {

    @Autowired
    private VectorStore vectorStore;


    /**
     * Params are not validated
     * The entity which use it must do it
     */
    @Override
    public void addVectorStore(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty())
            return;
        /*
          Text is embed automatically by the model
          indicated in application.yml
         */
        List<Document> documents = tasks.stream()
                .filter(input -> input.getId() != null && !input.getId().toString().isBlank())
                .filter(input -> input.getBody() != null && !input.getBody().isBlank())
                .map(task -> {
                    String documentContent = task.getBody();

                    // Relevant properties for the LLM
                    Map<String, Object> metadata = Map.of(
                            "task_id", task.getId(),
                            "isCompleted", task.isCompleted(),
                            "task_priority", task.getPriority(),
                            "task_date", task.getTaskDate(),
                            "task_user", task.getUser().getUsername()
                    );

                    return new Document(documentContent, metadata);
                })
                .toList();

        vectorStore.add(documents);
    }

    @Override
    public void deleteVectorStore(Integer id) {
        if (id == null || id < 1)
            return;

        String filterExpression = "task_id == " + id;
        vectorStore.delete(filterExpression);
    }

    @Override
    public void updateVectorStore(Task task) {
        if (task.getId() == null || task.getId() < 1)
            return;

        deleteVectorStore(task.getId());
        addVectorStore(List.of(task));
    }

    @Override
    public List<Integer> similaritySearch(String body, String username) {
        if (body == null || body.isEmpty())
            throw new InvalidTaskBodyException("Invalid body parameter");
        if (username == null || username.isEmpty())
            throw new InvalidUsernameException("Username must be not null");

        // Filter by current username
        String filterExpression = "task_user == \"" + username + "\"";
        // Fetch similarities in vector db
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(body)
                        .filterExpression(filterExpression)
                        .build()
        );

        if (results == null || results.isEmpty())
            return List.of();

        // For each result gets its task_id from metadata
        return results.stream()
                .map(result ->
                        (Integer) result.getMetadata().get("task_id")
                ).toList();
    }
}

