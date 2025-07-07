package com.juangomez.todoapp.service;

import com.juangomez.todoapp.dto.LoginRequest;
import com.juangomez.todoapp.dto.UserRegisterRequest;
import com.juangomez.todoapp.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    ResponseEntity<String> login (LoginRequest user);

    ResponseEntity<UserResponse> register(UserRegisterRequest user);
}
