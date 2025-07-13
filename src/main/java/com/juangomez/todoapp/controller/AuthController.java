package com.juangomez.todoapp.controller;

import com.juangomez.todoapp.dto.EmailRequest;
import com.juangomez.todoapp.dto.UserRegisterRequest;
import com.juangomez.todoapp.dto.UserResponse;
import com.juangomez.todoapp.service.authentication.AuthService;
import com.juangomez.todoapp.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Valid
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegisterRequest registerRequest)
            throws RoleNotFoundException {

        UserResponse userResponse = authService.register(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody EmailRequest emailRequest) {

        authService.forgotPassword(emailRequest);

        return ResponseEntity.ok(Map.of(
                "message", "If the email is registered you will receive a password link"
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build(); // 204 (No content)
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<Void> resetPasswordTokenValidation(
           @NotBlank @RequestParam String token
    ) {
        authService.resetPasswordTokenValidation(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @NotBlank @RequestParam String password,
            @NotBlank @RequestParam String token
    ) {
        authService.changePassword(password, token);
        return ResponseEntity.ok().build();
    }
}
