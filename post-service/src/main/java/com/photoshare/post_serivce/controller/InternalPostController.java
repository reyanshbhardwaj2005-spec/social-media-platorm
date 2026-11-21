package com.photoshare.post_serivce.controller;

import com.photoshare.post_serivce.dto.response.PostPageResponse;
import com.photoshare.post_serivce.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/post")
public class InternalPostController {

    private final PostService postService;

    public InternalPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/feed")
    public ResponseEntity<PostPageResponse> getFeed(
            @RequestParam List<UUID> userIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(postService.getFeed(userIds, page, size));
    }
}
