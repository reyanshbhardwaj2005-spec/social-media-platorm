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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PostMapping(value = "/profile", consumes = "multipart/form-data")
    public ResponseEntity<ProfileResponse> createProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @ModelAttribute ProfileRequest request,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

        ProfileResponse response = profileService.createProfile(authenticatedUserId(jwt), authenticatedUsername(jwt), request, profileImage, jwt.getTokenValue());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(profileService.getMyProfile(authenticatedUserId(jwt)));
    }

    @PutMapping(value = "/me", consumes = "multipart/form-data")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @ModelAttribute ProfileRequest request,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

        return ResponseEntity.ok(profileService.updateMyProfile(authenticatedUserId(jwt), request, profileImage, jwt.getTokenValue()));
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

    //this is a special endpoint that has been made for LikeService for that boolean error
    @GetMapping("/{postId}/exists")
    public ResponseEntity<Boolean> postExists(@PathVariable UUID postId) {

        boolean exists = profileService.existsByUserId(postId);

        return ResponseEntity.ok(exists);
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