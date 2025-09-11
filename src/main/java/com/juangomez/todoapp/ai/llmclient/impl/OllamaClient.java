package com.juangomez.todoapp.ai.llmclient.impl;

import com.juangomez.todoapp.ai.llmclient.LLMClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class OllamaClient implements LLMClient {

    @Autowired
    private OllamaChatModel ollamaChatModel;

    @Override
    public ChatResponse generate(String prompt) {
        return ollamaChatModel.call(new Prompt(prompt));
    }
}
