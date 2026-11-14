package com.photoshare.media_serivce.controller;

import com.photoshare.media_serivce.dto.MedialResponse;
import com.photoshare.media_serivce.enums.MediaCategory;
import com.photoshare.media_serivce.service.MediaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MedialResponse> upload(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam MediaCategory mediaCategory,
            @RequestPart("file") MultipartFile file) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                mediaService.uploadImage(authenticatedUserId(jwt), mediaCategory, file));

    }

    @GetMapping("/{mediaId}")
    public ResponseEntity<MedialResponse> getMedia(@PathVariable UUID mediaId) {
        return ResponseEntity.ok(mediaService.getMedia(mediaId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<MedialResponse>> getMyMedia(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(mediaService.getMyMedia(authenticatedUserId(jwt)));
    }

    @GetMapping("/me/category/{mediaCategory}")
    public ResponseEntity<List<MedialResponse>> getMyMediaByCategory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable MediaCategory mediaCategory) {
        return ResponseEntity.ok(mediaService.getMyMediaByCategory(authenticatedUserId(jwt), mediaCategory));
    }

    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Map<String, String>> deleteMedia(
            @PathVariable UUID mediaId,
            @AuthenticationPrincipal Jwt jwt) {
        mediaService.deleteMedia(mediaId, authenticatedUserId(jwt));
        return ResponseEntity.ok(Map.of("message", "Media deleted successfully."));
    }

    private UUID authenticatedUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            throw new IllegalArgumentException("A valid access token is required.");
        }
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Access token contains an invalid user ID.");
        }
    }
}
