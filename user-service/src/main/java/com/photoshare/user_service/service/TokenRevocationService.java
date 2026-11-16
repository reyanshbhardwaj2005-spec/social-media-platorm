package com.photoshare.user_service.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenRevocationService {
    private final StringRedisTemplate redisTemplate;

    public TokenRevocationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isRevoked(String jti) {
        if (jti == null || jti.isBlank()) {
            return true;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey("revoked:" + jti));
    }
}
