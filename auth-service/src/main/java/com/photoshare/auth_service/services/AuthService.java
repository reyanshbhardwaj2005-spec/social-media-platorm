package com.photoshare.auth_service.services;

import com.photoshare.auth_service.dto.request.LoginRequest;
import com.photoshare.auth_service.dto.request.SignupRequest;
import com.photoshare.auth_service.dto.response.AuthResponse;
import com.photoshare.auth_service.dto.response.UserResponse;
import com.photoshare.auth_service.entity.User;
import com.photoshare.auth_service.enums.AuthProvider;
import com.photoshare.auth_service.enums.UserRole;
import com.photoshare.auth_service.repository.UserRepository;
import com.photoshare.auth_service.security.JwtService;

import com.photoshare.auth_service.exceptions.DuplicateRequestException;

import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwt;
    private final RefreshTokenService refreshTokens;
    private final TokenRevocationService tokenRevocationService;

    public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager authenticationManager, JwtService jwt, RefreshTokenService refreshTokens, TokenRevocationService tokenRevocationService) {
        this.users = users;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
        this.refreshTokens = refreshTokens;
        this.tokenRevocationService = tokenRevocationService;
    }

    @Transactional
    public @Nullable AuthResponse signup(@Valid SignupRequest signupRequest) {

        String email = signupRequest.getEmail();
        String username = signupRequest.getUsername();

        if (users.existsByEmail(email)) throw new DuplicateRequestException("Email is already registered");
        if(users.existsByUsername(username)) throw new DuplicateRequestException("Username is already registered");

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(UserRole.USER);
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        user.setProvider(AuthProvider.LOCAL);
        users.save(user);
        return issue(user);

    }

    @Transactional
    public AuthResponse login(@Valid LoginRequest request) {

        String login = request.getLogin().trim();
        User user = users.findByEmailIgnoreCase(login).or(() -> users.findByUsernameIgnoreCase(login))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (user.getProvider() != AuthProvider.LOCAL) {
            throw new IllegalArgumentException("This account uses " + user.getProvider() + " login. Please continue with " + user.getProvider() + ".");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, request.getPassword()));
        return issue(user);
    }


    @Transactional
    public AuthResponse refresh(String rawToken) {
        User user = refreshTokens.validate(rawToken, users);
        refreshTokens.revoke(rawToken);
        return issue(user);
    }

    @Transactional
    public void logout(String rawRefreshToken, String accessToken) {

        // Revoke refresh token
        refreshTokens.revoke(rawRefreshToken);

        // Revoke access token
        String jti = jwt.extractTokenId(accessToken);
        Instant expiration = jwt.extractExpiration(accessToken);

        tokenRevocationService.revoke(jti, expiration);
    }

    @Transactional
    public AuthResponse issue(User user) {
        String access = jwt.generateAccessToken(user);
        String refresh = refreshTokens.create(user);
        return new AuthResponse("Bearer", access, refresh, jwt.getAccessExpirationMs(), UserResponse.from(user));
    }
}
