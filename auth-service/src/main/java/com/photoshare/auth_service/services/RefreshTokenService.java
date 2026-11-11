package com.photoshare.auth_service.services;

import com.photoshare.auth_service.entity.RefreshToken;
import com.photoshare.auth_service.entity.User;
import com.photoshare.auth_service.repository.RefreshTokenRepository;
import com.photoshare.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final long expirationMs;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository repository,
                               @Value("${app.jwt.refresh-token-expiration-ms:2592000000}") long expirationMs) {
        this.repository = repository;
        this.expirationMs = expirationMs;
    }

    @Transactional
    public User validate(String rawToken, UserRepository users) {
        RefreshToken token = repository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        if (token.isRevoked() || token.getExpiresAt().isBefore(Instant.now()))
            throw new IllegalArgumentException("Refresh token expired or revoked");
        return users.findById(token.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User no longer exists"));
    }

    @Transactional
    public void revoke(String rawToken) {
        repository.findByTokenHash(hash(rawToken)).ifPresent(token -> {
            token.setRevoked(true);
            repository.save(token);
        });
    }

    @Transactional
    public String create(User user) {
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        RefreshToken entity = new RefreshToken();
        entity.setUserId(user.getId());
        entity.setTokenHash(hash(raw));
        entity.setExpiresAt(Instant.now().plusMillis(expirationMs));
        repository.save(entity);
        return raw;
    }

    private String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
}
