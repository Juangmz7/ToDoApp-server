package com.juangomez.todoapp.ai.provider.impl;

import com.juangomez.todoapp.ai.provider.TranscriptionProvider;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class OpenAiTranscriptionProvider implements TranscriptionProvider {

    private OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;

    public OpenAiTranscriptionProvider(OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel) {
        this.openAiAudioTranscriptionModel = openAiAudioTranscriptionModel;
    }

    @Override
    public String audioToText(MultipartFile audioFile) {

        OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions
                .builder()
                .language("es")
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .build();

        AudioTranscriptionPrompt audioTranscriptionPrompt = new AudioTranscriptionPrompt(
                audioFile.getResource(), transcriptionOptions
        );
        return openAiAudioTranscriptionModel.call(audioTranscriptionPrompt)
                .getResult().getOutput();
    }
}
