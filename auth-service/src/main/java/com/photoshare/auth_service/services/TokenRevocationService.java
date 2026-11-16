package com.photoshare.auth_service.services;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenRevocationService {

    private static final String PREFIX = "revoked:";
    private final StringRedisTemplate redisTemplate;

    public TokenRevocationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void revoke(String jti, Instant expiration) {

        long remainingSeconds = Duration.between(Instant.now(), expiration).getSeconds();
        if (remainingSeconds <= 0) {
            return;
        }
        redisTemplate.opsForValue().set(PREFIX + jti, "revoked", Duration.ofSeconds(remainingSeconds));
    }

    public boolean isRevoked(String jti) {
        if (jti == null || jti.isBlank()) {
            return true;
        }

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(PREFIX + jti)
        );
    }
}
