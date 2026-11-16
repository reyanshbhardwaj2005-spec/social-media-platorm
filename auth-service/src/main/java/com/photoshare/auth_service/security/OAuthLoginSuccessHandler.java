package com.photoshare.auth_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.photoshare.auth_service.entity.User;
import com.photoshare.auth_service.services.AuthService;
import com.photoshare.auth_service.services.OAuthAccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuthAccountService accounts;
    private final AuthService auth;
    private final ObjectMapper objectMapper;

    public OAuthLoginSuccessHandler(OAuthAccountService accounts, AuthService auth, ObjectMapper objectMapper) {
        this.accounts = accounts;
        this.auth = auth;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;

        User user = accounts.resolve(oauth.getAuthorizedClientRegistrationId(), (OAuth2User) oauth.getPrincipal()
        );

        var tokens = auth.issue(user);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), tokens);
    }
}
