package com.hotketok.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final StringRedisTemplate redisTemplate;

    public void save(Long userId, String refreshToken, long ttlMs) {
        redisTemplate.opsForValue().set("refresh:" + userId, refreshToken, ttlMs, TimeUnit.MILLISECONDS);
    }

    public String find(Long userId) {
        return redisTemplate.opsForValue().get("refresh:" + userId);
    }

    public void delete(Long userId) {
        redisTemplate.delete("refresh:" + userId);
    }
}
