package com.fpoly.thainv.services;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fpoly.thainv.models.User;

@Service
public class TemporaryUserService {

    private final RedisTemplate<String, User> redisTemplate;

    public TemporaryUserService(RedisTemplate<String, User> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveTemporaryUser(User user) {
        redisTemplate.opsForValue().set(user.getPhone(), user, Duration.ofMinutes(10));
    }

    public User getTemporaryUser(String phone) {
        return redisTemplate.opsForValue().get(phone);
    }

    public void deleteTemporaryUser(String phone) {
        redisTemplate.delete(phone);
    }
}