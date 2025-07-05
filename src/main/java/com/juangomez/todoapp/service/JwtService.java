package com.juangomez.todoapp.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

public interface JwtService {

    /**
     *  Generates the secret key
     */
    public String generateSecretKey() throws NoSuchAlgorithmException;

    /**
     * Generates a token with the proportionate key
     */
    public String generateToken(String username);

    /**
     * Takes the username from the token
     */
    public String extractUsername(String token);

    /**
     *  Token validation
     *  Looks for date expiration and valid username
     */
    public boolean validateToken(String token, UserDetails userDetails);

}
