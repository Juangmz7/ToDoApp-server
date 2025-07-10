package com.juangomez.todoapp.ai.service;

import com.juangomez.todoapp.dto.DocumentInput;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VectorStoreService {

    public void addVectorStore(List<DocumentInput> documentInputs);

    public void deleteVectorStore(List<Integer> ids);

    public void updateVectorStore(List<DocumentInput> documentInputs);

    public List<Integer> similaritySearch(String body);

}
