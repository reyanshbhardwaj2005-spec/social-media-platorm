package com.photoshare.social_serivce.controller;

import com.photoshare.social_serivce.dto.response.ApiMessage;
import com.photoshare.social_serivce.dto.response.CountResponse;
import com.photoshare.social_serivce.dto.response.LikeStatusResponse;
import com.photoshare.social_serivce.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/social/like")
public class LikeController {

    private final LikeService service;

    public LikeController(LikeService service) {
        this.service = service;
    }

    private UUID current(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    @PostMapping("/{postId}")
    public ResponseEntity<LikeStatusResponse> like(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID postId) {
        return ResponseEntity.ok(service.like(current(jwt),postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiMessage> unlike(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID postId) {
        service.unlike(current(jwt),postId);return ResponseEntity.ok(new ApiMessage("Post unliked successfully."));
    }

    @GetMapping("/{postId}/status")
    public LikeStatusResponse status(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID postId) {
        return service.status(current(jwt),postId);
    }

    @GetMapping("/{postId}/count")
    public CountResponse count(@PathVariable UUID postId) {
        return service.count(postId);
    }
}
