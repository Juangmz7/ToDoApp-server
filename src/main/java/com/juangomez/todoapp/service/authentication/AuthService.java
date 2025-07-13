package com.juangomez.todoapp.service.authentication;

import com.juangomez.todoapp.dto.EmailRequest;
import com.juangomez.todoapp.dto.LoginRequest;
import com.juangomez.todoapp.dto.UserRegisterRequest;
import com.juangomez.todoapp.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;

@Service
public interface AuthService {
    String login (LoginRequest user);

    UserResponse register(UserRegisterRequest user) throws RoleNotFoundException;

    void forgotPassword(EmailRequest emailRequest);

    void logout(HttpServletRequest request);

    void resetPasswordTokenValidation(String token);

    void changePassword(String password, String token);
}
