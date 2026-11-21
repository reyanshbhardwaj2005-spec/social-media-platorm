package com.photoshare.social_serivce.controller;

import com.photoshare.social_serivce.service.FeedService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/social/feed")
public class FeedController {

    private final FeedService service;

    public FeedController(FeedService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String,Object> feed(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {

        return service.getFeed(UUID.fromString(jwt.getSubject()), page, size, jwt.getTokenValue());
    }
}
