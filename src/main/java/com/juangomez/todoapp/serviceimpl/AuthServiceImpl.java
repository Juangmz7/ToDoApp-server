package com.juangomez.todoapp.serviceimpl;

import com.juangomez.todoapp.dto.UserRegisterRequest;
import com.juangomez.todoapp.dto.UserResponse;
import com.juangomez.todoapp.model.User;
import com.juangomez.todoapp.repository.UserRepository;
import com.juangomez.todoapp.service.JwtService;
import com.juangomez.todoapp.service.UserService;
import com.juangomez.todoapp.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public String login(LoginRequest request) {
        try {
            // User authentication with username & password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Token generation if the authentication succeeded
            String jwt = jwtService.generateToken(request.getUsername());

            return ResponseEntity.ok(jwt);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid username or password");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @Override
    public UserResponse register(UserRegisterRequest registerRequest) {
        if (registerRequest == null) {
        }
        if (registerRequest.getUserName() == null || registerRequest.getUserName().trim().isEmpty()) {
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 8) {
        }


}
