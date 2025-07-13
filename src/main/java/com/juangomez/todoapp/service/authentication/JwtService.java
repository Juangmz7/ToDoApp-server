package com.juangomez.todoapp.service.authentication;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public interface JwtService {

    /**
     * Generates a token with the proportionate key
     */
    String generateToken(String username);

    /**
     * Takes the username from the token
     */
    String extractUsername(String token);

    /**
     *  Token validation
     *  Looks for date expiration and valid username
     */
    boolean validateToken(String token, UserDetails userDetails);

    String extractAuthToken(HttpServletRequest request);

    Duration tokenTtl(String token);

}
