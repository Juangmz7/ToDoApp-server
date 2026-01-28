package com.juangomez.todoapp.repository;

import com.juangomez.todoapp.config.TestContainersConfig;
import com.juangomez.todoapp.dto.UserRegisterRequest;
import com.juangomez.todoapp.dto.UserResponse;
import com.juangomez.todoapp.dto.LoginRequest;
import com.juangomez.todoapp.model.Role;
import com.juangomez.todoapp.model.enums.RoleName;
import com.juangomez.todoapp.service.authentication.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.juangomez.todoapp.service.MailService;

import javax.management.relation.RoleNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfig.class)
class AuthServiceIntegrationTest {

    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    @MockBean
    private MailService mailService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role role = new Role();
        role.setName(RoleName.ROLE_USER.toString());
        roleRepository.save(role);
    }

    @Test
    void shouldRegisterAndLoginUserSuccessfully() throws RoleNotFoundException {
        UserRegisterRequest registerReq = new UserRegisterRequest(
                "integrationUser",
                "password1234",
                "integration@test.com"
        );

        UserResponse userResponse = authService.register(registerReq);

        // Assert Registration
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userRepository.existsByUsername("integrationUser")).isTrue();

        // 2. Act: Login
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername("integrationUser");
        loginReq.setPassword("password1234");

        String token = authService.login(loginReq);

        // Assert Login
        assertThat(token).isNotNull();
        assertThat(token).startsWith("eyJ"); // Basic JWT check
    }
}