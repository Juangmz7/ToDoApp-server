package com.juangomez.todoapp.ai.provider;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface TranscriptionProvider {

    String audioToText(MultipartFile audioFile);

}
