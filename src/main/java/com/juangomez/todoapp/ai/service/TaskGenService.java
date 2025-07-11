package com.juangomez.todoapp.ai.service;

import com.juangomez.todoapp.dto.TaskRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface TaskGenService {

    TaskRequest taskRequestGenator(MultipartFile file);
}
