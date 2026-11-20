package com.photoshare.social_serivce.controller;

import com.photoshare.social_serivce.dto.request.CommentRequest;
import com.photoshare.social_serivce.dto.response.ApiMessage;
import com.photoshare.social_serivce.dto.response.CommentResponse;
import com.photoshare.social_serivce.dto.response.CountResponse;
import com.photoshare.social_serivce.dto.response.PageResponse;
import com.photoshare.social_serivce.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/social/comment")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    private UUID current(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponse> create(@AuthenticationPrincipal Jwt jwt,
                                                  @PathVariable UUID postId,
                                                  @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(service.create(current(jwt),postId,request));
    }

    @PutMapping("/{commentId}")
    public CommentResponse update(@AuthenticationPrincipal Jwt jwt,
                                  @PathVariable UUID commentId,
                                  @Valid @RequestBody CommentRequest request) {
        return service.update(current(jwt),commentId,request);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiMessage> delete(@AuthenticationPrincipal Jwt jwt,
                                             @PathVariable UUID commentId) {
        service.delete(current(jwt),commentId);
        return ResponseEntity.ok(new ApiMessage("Comment deleted successfully."));
    }

    @GetMapping("/post/{postId}")
    public PageResponse<CommentResponse> getByPost(@PathVariable UUID postId,
                                                   @RequestParam(defaultValue="0") int page,
                                                   @RequestParam(defaultValue="20") int size) {
        return service.getByPost(postId,page,size);
    }

    @GetMapping("/post/{postId}/count")
    public CountResponse count(@PathVariable UUID postId) {
        return service.count(postId);
    }
}
