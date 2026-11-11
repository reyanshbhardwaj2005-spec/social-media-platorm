package com.photoshare.auth_service.services;

import com.photoshare.auth_service.entity.User;
import com.photoshare.auth_service.enums.AuthProvider;
import com.photoshare.auth_service.enums.UserRole;
import com.photoshare.auth_service.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class OAuthAccountService {

    private final UserRepository users;
    public OAuthAccountService(UserRepository users) { this.users = users; }

    @Transactional
    public User resolve(String registrationId, OAuth2User principal) {

        AuthProvider provider = switch (registrationId.toLowerCase(Locale.ROOT)) {
            case "google" -> AuthProvider.GOOGLE;
            case "github" -> AuthProvider.GITHUB;
            default -> throw new IllegalArgumentException("Unsupported OAuth provider");
        };

        String attributeName = provider == AuthProvider.GOOGLE ? "sub" : "id";
        Object subjectAttribute = principal.getAttribute(attributeName);

        if (subjectAttribute == null) {
            throw new IllegalArgumentException("OAuth provider did not return the required user ID: " + attributeName);
        }

        String subject = subjectAttribute.toString();
        String email = principal.getAttribute("email");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("OAuth provider did not return an email address");

        return users.findByProviderAndProviderSubject(provider, subject).orElseGet(() -> {
            User user = users.findByEmailIgnoreCase(email).orElseGet(User::new);
            if (user.getId() != null && user.getProvider() == AuthProvider.LOCAL)
                throw new IllegalArgumentException("Email already belongs to a password account. Sign in normally first.");
            user.setEmail(email.toLowerCase(Locale.ROOT));
            user.setUsername(uniqueUsername(principal, email));
            user.setProvider(provider);
            user.setProviderSubject(subject);
            user.setRole(UserRole.USER);
            user.setEnabled(true);
            user.setAccountLocked(false);
            return users.save(user);
        });
    }

    private String uniqueUsername(OAuth2User principal, String email) {
        String candidate = principal.getAttribute("login");
        if (candidate == null || candidate.isBlank()) candidate = email.substring(0, email.indexOf('@'));
        candidate = candidate.replaceAll("[^A-Za-z0-9_.]", "");
        if (candidate.length() < 3) candidate = "user" + UUID.randomUUID().toString().substring(0, 8);
        candidate = candidate.substring(0, Math.min(45, candidate.length()));
        String result = candidate;
        int suffix = 1;
        while (users.existsByUsernameIgnoreCase(result)) {
            String tail = String.valueOf(suffix++);
            result = candidate.substring(0, Math.min(candidate.length(), 49 - tail.length())) + tail;
        }
        return result;
    }

}
