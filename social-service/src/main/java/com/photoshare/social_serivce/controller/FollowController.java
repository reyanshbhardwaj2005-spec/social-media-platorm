package com.photoshare.social_serivce.controller;

import com.photoshare.social_serivce.dto.response.*;
import com.photoshare.social_serivce.service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/social/follow")
public class FollowController {

    private final FollowService service;

    public FollowController(FollowService service) {
        this.service = service;
    }

    private UUID current(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    @PostMapping("/follow/{userId}")
    public ResponseEntity<FollowResponse> follow(@AuthenticationPrincipal Jwt jwt,
                                                 @PathVariable UUID userId) {
        return ResponseEntity.ok(service.follow(current(jwt),userId));
    }

    @GetMapping("/followers/{userId}")
    public PageResponse<FollowResponse> followers(@PathVariable UUID userId,
                                                  @RequestParam(defaultValue="0") int page,
                                                  @RequestParam(defaultValue="20") int size) {
        return service.followers(userId,page,size);
    }

    @GetMapping("/following/{userId}")
    public PageResponse<FollowResponse> following(@PathVariable UUID userId,
                                                  @RequestParam(defaultValue="0") int page,
                                                  @RequestParam(defaultValue="20") int size) {
        return service.following(userId,page,size);
    }

    @GetMapping("/status/{userId}")
    public FollowStatusResponse status(@AuthenticationPrincipal Jwt jwt,
                                       @PathVariable UUID userId) {
        return service.status(current(jwt),userId);
    }

    @GetMapping("/followers/{userId}/count")
    public CountResponse followerCount(@PathVariable UUID userId) {
        return service.followerCount(userId);
    }

    @GetMapping("/following/{userId}/count")
    public CountResponse followingCount(@PathVariable UUID userId) {
        return service.followingCount(userId);
    }

    @DeleteMapping("/unfollow/{userId}")
    public ResponseEntity<ApiMessage> unfollow(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable UUID userId) {
        service.unfollow(current(jwt),userId);
        return ResponseEntity.ok(new ApiMessage("Unfollowed successfully."));
    }
}
