package com.juangomez.todoapp.serviceimpl.authentication;

import com.juangomez.todoapp.service.authentication.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void blacklistToken(String token, Duration ttl) {
        redisTemplate.opsForValue().set("blacklist: " + token, "true", ttl);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.opsForValue().get("blacklist: " + token) != null;
    }

    @Override
    public void addToken(String username, String token, Duration ttl) {
        redisTemplate.opsForValue().set(username, token, ttl);
    }

    @Override
    public boolean isTokenValid (String token) {
        // If the token is in db
        return redisTemplate.opsForValue().get(token) != null;
    }

    @Override
    public String getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
