package com.photoshare.user_service.controller;

import com.photoshare.user_service.dto.request.PrivacyRequest;
import com.photoshare.user_service.dto.request.ProfileRequest;
import com.photoshare.user_service.dto.response.ProfileResponse;
import com.photoshare.user_service.dto.response.ProfileSummaryResponse;
import com.photoshare.user_service.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserProfileController {

    private final UserProfileService profileService;

    public UserProfileController(UserProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/profile")
    public ResponseEntity<ProfileResponse> createProfile(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProfileRequest request) {

        UUID userId = authenticatedUserId(jwt);
        String username = authenticatedUsername(jwt);

        ProfileResponse response = profileService.createProfile(userId, username, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(profileService.getMyProfile(authenticatedUserId(jwt)));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateMyProfile(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(profileService.updateMyProfile(authenticatedUserId(jwt), request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable UUID userId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(profileService.getProfile(userId, authenticatedUserId(jwt)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProfileSummaryResponse>> searchProfiles(@RequestParam("query") String query, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(profileService.searchProfiles(query, authenticatedUserId(jwt)));
    }

    @PatchMapping("/me/privacy")
    public ResponseEntity<ProfileResponse> updatePrivacy(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody PrivacyRequest request) {
        return ResponseEntity.ok(profileService.updatePrivacy(authenticatedUserId(jwt), request.getPrivateAccount()));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> deleteMyProfile(@AuthenticationPrincipal Jwt jwt) {
        // Profile deletion is intentionally not exposed in this MVP. Deleting a profile
        // should later be coordinated with posts, media, and social relationships.
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of("message", "Profile deletion is not supported yet."));
    }

    private UUID authenticatedUserId(Jwt jwt) {
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (Exception ex) {
            throw new IllegalArgumentException("JWT subject must contain the auth-service user UUID.");
        }
    }

    private String authenticatedUsername(Jwt jwt) {
        String username = jwt.getClaimAsString("username");

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("JWT must contain a valid username claim.");
        }

        return username.trim();
    }

}








//package com.photoshare.user_service.controller;
//
//import com.photoshare.user_service.dto.request.PrivacyRequest;
//import com.photoshare.user_service.dto.request.ProfileRequest;
//import com.photoshare.user_service.dto.response.ProfileResponse;
//import com.photoshare.user_service.dto.response.ProfileSummaryResponse;
//import com.photoshare.user_service.service.UserProfileService;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.web.bind.annotation.*;
//
//        import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/v1/users")
//public class UserProfileController {
//
//    private final UserProfileService profileService;
//
//    public UserProfileController(UserProfileService profileService) {
//        this.profileService = profileService;
//    }
//
//    @PostMapping("/profile")
//    public ResponseEntity<ProfileResponse> createProfile(
//            @AuthenticationPrincipal Jwt jwt,
//            @Valid @RequestBody ProfileRequest request) {
//
//        UUID userId = authenticatedUserId(jwt);
//        String username = authenticatedUsername(jwt);
//
//        ProfileResponse response =
//                profileService.createProfile(userId, username, request);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @GetMapping("/me")
//    public ResponseEntity<ProfileResponse> getMyProfile(
//            @AuthenticationPrincipal Jwt jwt) {
//
//        return ResponseEntity.ok(
//                profileService.getMyProfile(authenticatedUserId(jwt))
//        );
//    }
//
//    @PutMapping("/me")
//    public ResponseEntity<ProfileResponse> updateMyProfile(
//            @AuthenticationPrincipal Jwt jwt,
//            @Valid @RequestBody ProfileRequest request) {
//
//        return ResponseEntity.ok(
//                profileService.updateMyProfile(authenticatedUserId(jwt), request)
//        );
//    }
//
//    @GetMapping("/{userId}")
//    public ResponseEntity<ProfileResponse> getProfile(
//            @PathVariable UUID userId,
//            @AuthenticationPrincipal Jwt jwt) {
//
//        return ResponseEntity.ok(
//                profileService.getProfile(userId, authenticatedUserId(jwt))
//        );
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<List<ProfileSummaryResponse>> searchProfiles(
//            @RequestParam("query") String query,
//            @AuthenticationPrincipal Jwt jwt) {
//
//        return ResponseEntity.ok(
//                profileService.searchProfiles(query, authenticatedUserId(jwt))
//        );
//    }
//
//    @PatchMapping("/me/privacy")
//    public ResponseEntity<ProfileResponse> updatePrivacy(
//            @AuthenticationPrincipal Jwt jwt,
//            @Valid @RequestBody PrivacyRequest request) {
//
//        return ResponseEntity.ok(
//                profileService.updatePrivacy(
//                        authenticatedUserId(jwt),
//                        request.getPrivateAccount()
//                )
//        );
//    }
//
//    @DeleteMapping("/me")
//    public ResponseEntity<Map<String, String>> deleteMyProfile(
//            @AuthenticationPrincipal Jwt jwt) {
//
//        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
//                .body(Map.of(
//                        "message",
//                        "Profile deletion is not supported yet."
//                ));
//    }
//
//    private UUID authenticatedUserId(Jwt jwt) {
//        try {
//            return UUID.fromString(jwt.getSubject());
//        } catch (Exception ex) {
//            throw new IllegalArgumentException(
//                    "JWT subject must contain the auth-service user UUID."
//            );
//        }
//    }
//
//    private String authenticatedUsername(Jwt jwt) {
//        String username = jwt.getClaimAsString("username");
//
//        if (username == null || username.isBlank()) {
//            throw new IllegalArgumentException(
//                    "JWT must contain a valid username claim."
//            );
//        }
//
//        return username.trim();
//    }
//}
