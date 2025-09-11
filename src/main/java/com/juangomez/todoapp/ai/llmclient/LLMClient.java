package com.juangomez.todoapp.ai.llmclient;

import org.springframework.ai.chat.model.ChatResponse;

public interface LLMClient {

    ChatResponse generate (String prompt);

}
