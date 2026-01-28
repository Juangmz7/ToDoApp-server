package com.juangomez.todoapp.service;

import com.juangomez.todoapp.config.exception.user.*;
import com.juangomez.todoapp.dto.EmailRequest;
import com.juangomez.todoapp.dto.LoginRequest;
import com.juangomez.todoapp.dto.UserRegisterRequest;
import com.juangomez.todoapp.model.User;
import com.juangomez.todoapp.repository.RoleRepository;
import com.juangomez.todoapp.repository.UserRepository;
import com.juangomez.todoapp.service.authentication.JwtService;
import com.juangomez.todoapp.service.authentication.TokenBlacklistService;
import com.juangomez.todoapp.serviceimpl.MailServiceImpl;
import com.juangomez.todoapp.serviceimpl.authentication.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private JwtService jwtService;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private TokenBlacklistService tokenBlacklistService;
    @Mock private MailService mailService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    // Only mocking MailServiceImpl statically as it contains the static helper method
    private MockedStatic<MailServiceImpl> mailServiceStaticMock;

    @BeforeEach
    void setUp() {
        mailServiceStaticMock = mockStatic(MailServiceImpl.class);
    }

    @AfterEach
    void tearDown() {
        mailServiceStaticMock.close();
    }

    // --- Login Tests ---

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("user", "pass");
        when(jwtService.generateToken("user")).thenReturn("jwt-token");

        String token = authService.login(request);

        assertNotNull(token);
        assertEquals("jwt-token", token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    // --- Register Tests ---

    @Test
    void register_NullRequest_ThrowsException() {
        assertThrows(InvalidUserException.class, () -> authService.register(null));
    }

    @Test
    void register_InvalidUsername_ThrowsException() {
        UserRegisterRequest req = new UserRegisterRequest(null, "pass1234", "test@test.com");
        assertThrows(InvalidUsernameException.class, () -> authService.register(req));

        req.setUsername("");
        assertThrows(InvalidUsernameException.class, () -> authService.register(req));
    }

    @Test
    void register_InvalidPassword_ThrowsException() {
        UserRegisterRequest req = new UserRegisterRequest("user", "short", "test@test.com");
        assertThrows(InvalidPasswordException.class, () -> authService.register(req));
    }

    @Test
    void register_InvalidEmail_ThrowsException() {
        UserRegisterRequest req = new UserRegisterRequest("user", "pass1234", "invalid-email");
        assertThrows(InvalidEmailException.class, () -> authService.register(req));
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        UserRegisterRequest req = new UserRegisterRequest("user", "pass1234", "test@test.com");
        when(userRepository.existsByUsername("user")).thenReturn(true);

        assertThrows(DuplicateUsernameException.class, () -> authService.register(req));
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        UserRegisterRequest req = new UserRegisterRequest("user", "pass1234", "test@test.com");
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> authService.register(req));
    }

    // --- Forgot Password Tests ---

    @Test
    void forgotPassword_InvalidEmail_ThrowsException() {
        EmailRequest req = new EmailRequest("invalid");
        assertThrows(InvalidEmailException.class, () -> authService.forgotPassword(req));
    }

    @Test
    void forgotPassword_UserNotFound_DoNothing() {
        EmailRequest req = new EmailRequest("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(null);

        authService.forgotPassword(req);

        verify(mailService, never()).sendForgotPasswordMail(anyString(), anyString());
    }

    @Test
    void forgotPassword_Success() {
        EmailRequest req = new EmailRequest("test@test.com");
        User user = new User();
        user.setUsername("user");
        user.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com")).thenReturn(user);
        mailServiceStaticMock.when(() -> MailServiceImpl.generateNumericCode(8)).thenReturn("12345678");

        authService.forgotPassword(req);

        verify(tokenBlacklistService).addToken(eq("user"), eq("12345678"), any(Duration.class));
        verify(tokenBlacklistService).addToken(eq("12345678"), eq("user"), any(Duration.class));
        verify(mailService).sendForgotPasswordMail("12345678", "test@test.com");
    }

    // --- Logout Tests ---

    @Test
    void logout_NullToken_DoNothing() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(jwtService.extractAuthToken(req)).thenReturn(null);

        authService.logout(req);

        verify(tokenBlacklistService, never()).blacklistToken(anyString(), any());
    }

    @Test
    void logout_Success() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        String token = "valid.jwt.token";
        Duration duration = Duration.ofMinutes(30);

        when(jwtService.extractAuthToken(req)).thenReturn(token);
        when(jwtService.tokenTtl(token)).thenReturn(duration);

        authService.logout(req);

        verify(tokenBlacklistService).blacklistToken(token, duration);
    }

    // --- Reset Password Token Validation Tests ---

    @Test
    void resetPasswordTokenValidation_InvalidToken_ThrowsException() {
        when(tokenBlacklistService.isTokenValid("token")).thenReturn(false);
        assertThrows(IllegalStateException.class, () -> authService.resetPasswordTokenValidation("token"));
    }

    @Test
    void resetPasswordTokenValidation_ExpiredTokenOverride_ThrowsException() {
        String token = "token123";
        String username = "user";

        when(tokenBlacklistService.isTokenValid(token)).thenReturn(true);
        when(tokenBlacklistService.getObject(token)).thenReturn(username);
        // Returns a different token, implying override
        when(tokenBlacklistService.getObject(username)).thenReturn("newToken456");

        assertThrows(IllegalStateException.class, () -> authService.resetPasswordTokenValidation(token));
    }

    @Test
    void resetPasswordTokenValidation_Success() {
        String token = "token123";
        String username = "user";

        when(tokenBlacklistService.isTokenValid(token)).thenReturn(true);
        when(tokenBlacklistService.getObject(token)).thenReturn(username);
        when(tokenBlacklistService.getObject(username)).thenReturn(token);

        assertDoesNotThrow(() -> authService.resetPasswordTokenValidation(token));
    }

    // --- Change Password Tests ---

    @Test
    void changePassword_TokenValidationFails_ThrowsException() {
        String token = "invalid";
        when(tokenBlacklistService.isTokenValid(token)).thenReturn(false);
        assertThrows(IllegalStateException.class, () -> authService.changePassword("newPass", token));
    }

    @Test
    void changePassword_UsernameNotFoundInRedis_DoNothing() {
        String token = "valid";
        String username = "user";

        // Simulating validation passes but user retrieval fails logically
        when(tokenBlacklistService.isTokenValid(token)).thenReturn(true);
        when(tokenBlacklistService.getObject(token)).thenReturn(username).thenReturn(null);
        when(tokenBlacklistService.getObject(username)).thenReturn(token);

        authService.changePassword("newPass", token);

        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void changePassword_UserNotFoundInDB_DoNothing() {
        String token = "valid";
        String username = "user";

        when(tokenBlacklistService.isTokenValid(token)).thenReturn(true);
        when(tokenBlacklistService.getObject(token)).thenReturn(username);
        when(tokenBlacklistService.getObject(username)).thenReturn(token);
        when(userRepository.findByUsername(username)).thenReturn(null);

        authService.changePassword("newPass", token);

        verify(userRepository, never()).save(any());
    }

}