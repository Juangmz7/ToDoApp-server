package com.juangomez.todoapp.controller;

import com.juangomez.todoapp.dto.UserRegisterRequest;
import com.juangomez.todoapp.dto.UserResponse;
import com.juangomez.todoapp.model.User;
import com.juangomez.todoapp.repository.UserRepository;
import com.juangomez.todoapp.service.authentication.AuthService;
import com.juangomez.todoapp.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    UserRepository repo;

    // Testeo
    @GetMapping("/users")
    public List<User> getUsers() {
        return repo.findAll();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegisterRequest registerRequest) throws RoleNotFoundException {
        UserResponse userResponse = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userResponse);
    }
}
