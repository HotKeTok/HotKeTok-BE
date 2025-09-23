package com.hotketok.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class PhoneAuthRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "phone:";

    public void saveCode(String phone, String code, long ttlMillis) {
        redisTemplate.opsForValue().set(PREFIX + phone, code, ttlMillis, TimeUnit.MILLISECONDS);
    }

    public String findCode(String phone) {
        return redisTemplate.opsForValue().get(PREFIX + phone);
    }

    public void deleteCode(String phone) {
        redisTemplate.delete(PREFIX + phone);
    }

    public void markVerified(String phone, long ttlMillis) {
        redisTemplate.opsForValue().set(PREFIX + phone + ":verified", "true", ttlMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isVerified(String phone) {
        return "true".equals(redisTemplate.opsForValue().get(PREFIX + phone + ":verified"));
    }
}
