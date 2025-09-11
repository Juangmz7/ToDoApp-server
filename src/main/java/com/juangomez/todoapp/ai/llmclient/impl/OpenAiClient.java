package com.juangomez.todoapp.ai.llmclient.impl;

import com.juangomez.todoapp.ai.llmclient.LLMClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenAiClient implements LLMClient {

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Override
    public ChatResponse generate(String prompt) {
        return openAiChatModel.call(new Prompt(prompt));
    }

}
