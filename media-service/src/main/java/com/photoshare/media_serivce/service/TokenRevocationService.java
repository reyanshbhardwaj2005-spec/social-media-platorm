package com.photoshare.media_serivce.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenRevocationService {

    private static final String PREFIX = "revoked:";

    private final StringRedisTemplate redisTemplate;

    public TokenRevocationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isRevoked(String tokenId) {

        if (tokenId == null || tokenId.isBlank()) {
            return true;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + tokenId));
    }
}
