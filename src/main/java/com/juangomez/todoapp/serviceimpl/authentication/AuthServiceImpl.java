package com.juangomez.todoapp.serviceimpl.authentication;

import com.juangomez.todoapp.config.SecurityConfig;
import com.juangomez.todoapp.config.exception.user.*;
import com.juangomez.todoapp.dto.UserRegisterRequest;
import com.juangomez.todoapp.dto.UserResponse;
import com.juangomez.todoapp.model.Role;
import com.juangomez.todoapp.model.User;
import com.juangomez.todoapp.model.enums.RoleName;
import com.juangomez.todoapp.repository.RoleRepository;
import com.juangomez.todoapp.repository.UserRepository;
import com.juangomez.todoapp.service.authentication.JwtService;
import com.juangomez.todoapp.service.authentication.AuthService;
import com.juangomez.todoapp.dto.LoginRequest;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.HashSet;
import java.util.Set;


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
}
