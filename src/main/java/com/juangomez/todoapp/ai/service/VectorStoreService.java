package com.juangomez.todoapp.ai.service;

import com.juangomez.todoapp.model.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VectorStoreService {

    public void addVectorStore(List<Task> tasks);

    public void deleteVectorStore(Integer id);

    public void updateVectorStore(Task task);

    public List<Integer> similaritySearch(String body, String username);

}
