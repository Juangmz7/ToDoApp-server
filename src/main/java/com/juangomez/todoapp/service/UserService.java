package com.juangomez.todoapp.service;

import dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    ResponseEntity<String> login (LoginRequest user);
}
