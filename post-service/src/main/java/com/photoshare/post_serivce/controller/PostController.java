package com.photoshare.post_serivce.controller;

import com.photoshare.post_serivce.dto.request.UpdatePostRequest;
import com.photoshare.post_serivce.dto.response.PostPageResponse;
import com.photoshare.post_serivce.dto.response.PostResponse;
import com.photoshare.post_serivce.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String caption,
            @RequestPart("file") MultipartFile file,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(authenticatedUserId(jwt), caption, file, extractToken(authorizationHeader)));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping("/me")
    public ResponseEntity<PostPageResponse> getMyPosts(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getMyPosts(authenticatedUserId(jwt), page, size));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PostPageResponse> getPostsByUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getPostsByUser(userId, page, size));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID postId,
            @Valid @RequestBody UpdatePostRequest request) {
        return ResponseEntity.ok(postService.updatePost(authenticatedUserId(jwt), postId, request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID postId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        postService.deletePost(authenticatedUserId(jwt), postId, extractToken(authorizationHeader));
        return ResponseEntity.noContent().build();
    }


    private UUID authenticatedUserId(Jwt jwt) {
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (RuntimeException ex) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid user identity in access token.");
        }
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Bearer access token is required.");
        }
        return authorizationHeader.substring(7);
    }
}
