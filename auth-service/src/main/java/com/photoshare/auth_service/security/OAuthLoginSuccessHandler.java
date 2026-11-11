package com.photoshare.auth_service.security;

import com.photoshare.auth_service.entity.User;
import com.photoshare.auth_service.services.AuthService;
import com.photoshare.auth_service.services.OAuthAccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
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
    private final String redirectUri;

    public OAuthLoginSuccessHandler(OAuthAccountService accounts, AuthService auth,
                                     @Value("${app.oauth2.authorized-redirect-uri}") String redirectUri) {
        this.accounts = accounts; this.auth = auth; this.redirectUri = redirectUri;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;
        User user = accounts.resolve(oauth.getAuthorizedClientRegistrationId(), (OAuth2User) oauth.getPrincipal());
        var tokens = auth.issue(user);

        addCookie(response, "PS_ACCESS", tokens.accessToken, 15 * 60);
        addCookie(response, "PS_REFRESH", tokens.refreshToken, 30 * 24 * 60 * 60);
        response.sendRedirect(redirectUri);
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true).secure(false).sameSite("Lax").path("/")
                .maxAge(maxAge).build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
