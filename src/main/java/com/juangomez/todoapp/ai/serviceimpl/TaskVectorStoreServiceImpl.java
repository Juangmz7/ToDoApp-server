package com.juangomez.todoapp.ai.serviceimpl;

import com.juangomez.todoapp.config.exception.task.InvalidTaskBodyException;
import com.juangomez.todoapp.dto.DocumentInput;
import com.juangomez.todoapp.ai.service.VectorStoreService;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public void addVectorStore(List<DocumentInput> documentInputs) {
        if (documentInputs == null || documentInputs.isEmpty())
            return;
        /*
          Text is embed automatically by the model
          indicated in application.yml
         */
        List<Document> documents = documentInputs.stream()
                .filter(input -> input.getId() != null && !input.getId().toString().isBlank())
                .filter(input -> input.getBody() != null && !input.getBody().isBlank())
                .map(input -> Document
                        .builder()
                        .id(input.getId().toString())
                        .text(input.getBody())
                        .build()
                )
                .toList();

        vectorStore.add(documents);
    }

    @Override
    public void deleteVectorStore(List<Integer> ids) {
        vectorStore.delete(ids.stream()
                .filter(Objects::nonNull)
                .toList()
                .toString()
        );
    }

    @Override
    public void updateVectorStore(List<DocumentInput> documentInputs) {
        deleteVectorStore(documentInputs.stream()
                .map(DocumentInput::getId)
                .filter(Objects::nonNull) // Filters by not Null
                .collect(Collectors.toList())
        );
        addVectorStore(documentInputs);
    }

    @Override
    public List<Integer> similaritySearch(String body) {
        if (body == null || body.isEmpty())
            throw new InvalidTaskBodyException("Invalid body parameter");

        // Fetch similarities in vector db
//        List<Document> results = vectorStore.similaritySearch(
//                SearchRequest.builder()
//                        .query(body)
//                        .build()
//        );

        List<Document> results = vectorStore.similaritySearch(body);

        if (results == null || results.isEmpty())
            return List.of();

        return results.stream()
                .map(Document::getId)  // Get all ids
                .map(Integer::valueOf) // Parse String to Integer
                .toList();
    }
}

