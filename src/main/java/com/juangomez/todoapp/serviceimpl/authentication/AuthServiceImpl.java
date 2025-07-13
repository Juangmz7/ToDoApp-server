package com.juangomez.todoapp.serviceimpl.authentication;

import com.juangomez.todoapp.config.authentication.SecurityConfig;
import com.juangomez.todoapp.config.exception.user.*;
import com.juangomez.todoapp.dto.EmailRequest;
import com.juangomez.todoapp.dto.UserRegisterRequest;
import com.juangomez.todoapp.dto.UserResponse;
import com.juangomez.todoapp.model.Role;
import com.juangomez.todoapp.model.User;
import com.juangomez.todoapp.model.enums.RoleName;
import com.juangomez.todoapp.repository.RoleRepository;
import com.juangomez.todoapp.repository.UserRepository;
import com.juangomez.todoapp.service.MailService;
import com.juangomez.todoapp.service.authentication.JwtService;
import com.juangomez.todoapp.service.authentication.AuthService;
import com.juangomez.todoapp.dto.LoginRequest;
import com.juangomez.todoapp.service.authentication.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private MailService mailService;

    @Override
    public String login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        return jwtService.generateToken(request.getUsername());
    }

    @Override
    public UserResponse register(UserRegisterRequest registerRequest) throws RoleNotFoundException {
        // Validations for registerRequest
        if (registerRequest == null) {
            throw new InvalidUserException("Registration request cannot be null");
        }
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            throw new InvalidUsernameException("Invalid username");
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 8) {
            throw new InvalidPasswordException("Password must have at least 8 characters");
        }
        if (!EmailValidator.getInstance().isValid(registerRequest.getEmail())) {
            throw new InvalidEmailException("Invalid email format");
        }

        // Verify if the user already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateUsernameException("Username already exists");
        }

        // New user registration
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());

        // Password hashed
        String encodedPassword = SecurityConfig.bCryptPasswordEncoder
                .encode(registerRequest.getPassword());
        user.setPassword(encodedPassword);

        // Fetch roles in db
        Role role = roleRepository.findByName(RoleName.ROLE_USER.toString());

        if (role == null ) {
            throw new RoleNotFoundException();
        }

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        // Save user in db
        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail()
        );
    }

    @Override
    public void forgotPassword(EmailRequest emailRequest) {
        // Validates the email
        if (!EmailValidator.getInstance().isValid(emailRequest.getEmail())) {
            throw new InvalidEmailException("Invalid email format");
        }

        User user = userRepository.findByEmail(emailRequest.getEmail());

        // If it does not exist in db
        if (user == null) {
            return;
        }

        // Create the token
        String token = UUID.randomUUID().toString();

        // To search by token or username
        tokenBlacklistService.addToken(user.getUsername(), token, Duration.ofMinutes(15));
        tokenBlacklistService.addToken(token, user.getUsername(), Duration.ofMinutes(15));

        // Send the email
        String url = "https://localhost:8081/auth/validate-reset-token?token=" + token;
        mailService.sendForgotPasswordMail(token,"juanjuango@hotmail.com" , url);

    }

    @Override
    public void logout(HttpServletRequest request) {
        String token = jwtService.extractAuthToken(request);
        // The token is not valid
        if (token == null) {
            return;
        }
        // Add the token to the Blacklist
        tokenBlacklistService.blacklistToken(token, jwtService.tokenTtl(token));
    }

    @Override
    public void resetPasswordTokenValidation(String token) {

        // If the token is in db, then it is valid
        if (!tokenBlacklistService.isTokenValid(token)) {
            throw new IllegalStateException("The token provided is expired");
        }

        // Verify if the token is the last one sent
        String username = tokenBlacklistService.getObject(token);

        /*
            When a new token it's provided, last one is overridden.
            In db there are 2 objects for each request:

                - Key(token) : username -> This one is never overwritten
                - Key(Username) : token -> This one is

            The point it's to prove that the one which is override it's equal to
            the token provided in actual request

            This will verify it is the last generated one
        */
        if (!tokenBlacklistService.getObject(username).equals(token)) {
            throw new IllegalStateException("The token provided is not valid");
        }
    }

    @Override
    public void changePassword(String password, String token) {

        // Token verification
        resetPasswordTokenValidation(token);

        String username = tokenBlacklistService.getObject(token);

        if (username == null) {
            return;
        }

        User user = userRepository.findByUsername(username);

        if (user == null) {
            return;
        }

        // Password change and update
        String encryptedPassword = SecurityConfig.bCryptPasswordEncoder.encode(password);
        user.setPassword(encryptedPassword);

        userRepository.save(user);

    }
}
