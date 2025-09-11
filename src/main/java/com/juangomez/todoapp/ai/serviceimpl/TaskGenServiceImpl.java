package com.juangomez.todoapp.ai.serviceimpl;


import com.juangomez.todoapp.ai.llmclient.LLMClient;
import com.juangomez.todoapp.ai.provider.impl.OpenAiTranscriptionProvider;
import com.juangomez.todoapp.ai.service.TaskGenService;
import com.juangomez.todoapp.config.assistance.JsonAssistance;
import com.juangomez.todoapp.dto.TaskRequest;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;


@Service
public class TaskGenServiceImpl implements TaskGenService {

    @Autowired
    private LLMClient llmClient;

    @Autowired
    private OpenAiTranscriptionProvider openAiTranscriptionProvider;

    @Autowired
    private JsonAssistance jsonAssistance;

    @Override
    public TaskRequest taskRequestGenerator(MultipartFile file) {
        // From the file to plain text
        String text = openAiTranscriptionProvider.audioToText(file);
        String jsonPrompt = """
                        Convert the following text into a JSON with the fields:
                        - body (String) is the task
                        - taskDate (LocalDate yyyy-MM-dd)
                        - priority (enum of URGENT, HIGH, MEDIUM, LOW)
                        - completed or not
                       \s
                        If any of the fields are missing in the text, use the following default values:
                        - body: ""
                        - taskDate: null
                        - priority: MEDIUM
                        - completed (false)
                        \s
                        When date is taken, don´t include it in the body.
                        Do not include in body field the residual text when not including date.
                        Only include the actions on it
                       \s
                        The first part of the audio could be a verb referred to the real body action,
                        try to guess it and avoid including it.
                       \s
                        Do not answer with more information than the json specified
                    \s
                       \s
                        Text: "%s"
               \s""".formatted(text);
        // Text to json
        ChatResponse response = llmClient.generate(jsonPrompt);

        // The String json contains data in json format
        String json = response.getResult().getOutput().getText();
        // For testing
        System.out.println(json);

        // Json to TaskRequest
        TaskRequest taskRequest = jsonAssistance.jsonToEntity(json, TaskRequest.class);

        // Date validation
        if (taskRequest.getTaskDate() == null) {
            taskRequest.setTaskDate(LocalDate.now());
        }

        return taskRequest;
    }
}
