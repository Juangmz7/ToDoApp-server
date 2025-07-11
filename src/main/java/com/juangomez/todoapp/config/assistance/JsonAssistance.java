package com.juangomez.todoapp.config.assistance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.juangomez.todoapp.config.exception.task.InvalidTaskBodyException;
import org.springframework.stereotype.Component;

@Component
public class JsonAssistance {

    private final JsonMapper jsonMapper;

    public JsonAssistance() {
         jsonMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }

    public <T> T jsonToEntity(String json, Class<T> tClass) {
        T entity;

        try {
            entity = jsonMapper.readValue(json, tClass);

        } catch (JsonProcessingException e) {
            throw new InvalidTaskBodyException("JSON could´t been converted to " + tClass);
        }

        return entity;
    }

    public <T> String entityToJson(T entity) {
        String json = null;

        try {
            json = jsonMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

}
