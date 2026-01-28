package com.juangomez.todoapp.service;

import com.juangomez.todoapp.serviceimpl.authentication.TokenBlacklistServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceImplTest {

    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenBlacklistServiceImpl blacklistService;

    @Test
    void blacklistToken() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        blacklistService.blacklistToken("token", Duration.ofMinutes(1));
        verify(valueOperations).set("blacklist: token", "true", Duration.ofMinutes(1));
    }

    @Test
    void isTokenBlacklisted() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("blacklist: token")).thenReturn("true");
        assertTrue(blacklistService.isTokenBlacklisted("token"));

        when(valueOperations.get("blacklist: token")).thenReturn(null);
        assertFalse(blacklistService.isTokenBlacklisted("token"));
    }

}