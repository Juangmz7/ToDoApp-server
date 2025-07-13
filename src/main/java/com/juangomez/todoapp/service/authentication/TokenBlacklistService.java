package com.juangomez.todoapp.service.authentication;

import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public interface TokenBlacklistService {

    // Adds the token to the blacklist with a TTL
    void blacklistToken(String token, Duration ttl);

    // Verifies if the token is in the blacklist
    boolean isTokenBlacklisted(String token);

    // Verifies if token is in db
    void addToken(String username, String token, Duration ttl);

    boolean isTokenValid(String token);

    String getObject(String key);

}
