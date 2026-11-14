package com.photoshare.media_serivce.service;

import com.photoshare.media_serivce.dto.MedialResponse;
import com.photoshare.media_serivce.entity.Media;
import com.photoshare.media_serivce.enums.MediaCategory;
import com.photoshare.media_serivce.repository.MediaRepository;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class MediaService {

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    private final MediaRepository mediaRepository;
    private final CloudinaryService cloudinaryService;
    private final long maxFileSizeBytes;

    public MediaService(MediaRepository mediaRepository,
                        CloudinaryService cloudinaryService,
                        @Value("${app.media.max-file-size-bytes:10485760}") long maxFileSizeBytes) {
        this.mediaRepository = mediaRepository;
        this.cloudinaryService = cloudinaryService;
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    public @Nullable MedialResponse uploadImage(UUID userId, MediaCategory category, MultipartFile file) {

        validateFile(file);

        if (category == null) {
            throw new IllegalArgumentException("Media category is required.");
        }

        String folder = switch (category) {
            case PROFILE_PICTURE -> "Social Media Platform/profiles/" + userId;
            case POST_IMAGE -> "Social Media Platform/posts/" + userId;
        };

        if (category == MediaCategory.PROFILE_PICTURE) {
            return replaceProfilePicture(userId, file, folder);
        }

        Map uploadResult = cloudinaryService.uploadImage(file, folder);
        Media media = buildMedia(userId, category, file, uploadResult);
        return toResponse(mediaRepository.save(media));
    }

    private MedialResponse replaceProfilePicture(UUID userId, MultipartFile file, String folder) {

        Media existing = (Media) mediaRepository
                .findFirstByUserIdAndMediaCategory(userId, MediaCategory.PROFILE_PICTURE)
                .orElse(null);

        Map uploadResult = cloudinaryService.uploadImage(file, folder);
        Media uploaded = buildMedia(userId, MediaCategory.PROFILE_PICTURE, file, uploadResult);

        if (existing == null) {
            return toResponse(mediaRepository.save(uploaded));
        }

        String oldPublicId = existing.getCloudinaryPublicId();

        // Preserve mediaId, so User Service can keep the same media reference.
        existing.setCloudinaryPublicId(uploaded.getCloudinaryPublicId());
        existing.setImageUrl(uploaded.getImageUrl());
        existing.setFormat(uploaded.getFormat());
        existing.setFileSizeBytes(uploaded.getFileSizeBytes());
        existing.setWidth(uploaded.getWidth());
        existing.setHeight(uploaded.getHeight());

        Media saved = mediaRepository.save(existing);

        // Best-effort cleanup: new image is already active even if old asset cleanup fails.
        try {
            cloudinaryService.deleteImage(oldPublicId);
        } catch (RuntimeException ex) {
            log.warn("New profile picture saved, but old Cloudinary asset cleanup failed: {}", oldPublicId, ex);
        }
        return toResponse(saved);
    }


    public @Nullable MedialResponse getMedia(UUID mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found."));
        return toResponse(media);
    }

    public @Nullable List<MedialResponse> getMyMedia(UUID userId) {
        return mediaRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    public @Nullable List<MedialResponse> getMyMediaByCategory(UUID userId, MediaCategory category) {
        if (category == null) throw new IllegalArgumentException("Media category is required.");
        return mediaRepository.findAllByUserIdAndMediaCategoryOrderByCreatedAtDesc(userId, category)
                .stream().map(this::toResponse).toList();
    }

    public void deleteMedia(UUID mediaId, UUID userId) {
        Media media = (Media) mediaRepository.findByIdAndUserId(mediaId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Media not found or you do not have permission to delete it."));
        cloudinaryService.deleteImage(media.getCloudinaryPublicId());
        mediaRepository.delete(media);
    }

    @Transactional(readOnly = true)
    public boolean belongsToUser(UUID mediaId, UUID userId) {
        return mediaRepository.findByIdAndUserId(mediaId, userId).isPresent();
    }

    private Media buildMedia(UUID userId, MediaCategory category,
                             MultipartFile file, Map uploadResult) {
        String publicId = (String) uploadResult.get("public_id");
        String secureUrl = (String) uploadResult.get("secure_url");
        if (publicId == null || secureUrl == null) {
            throw new IllegalStateException("Cloudinary did not return the required image information.");
        }

        Number bytes = (Number) uploadResult.get("bytes");
        Number width = (Number) uploadResult.get("width");
        Number height = (Number) uploadResult.get("height");

        Media media = new Media();
        media.setUserId(userId);
        media.setMediaCategory(category);
        media.setCloudinaryPublicId(publicId);
        media.setImageUrl(secureUrl);
        media.setFormat((String) uploadResult.getOrDefault("format", "unknown"));
        media.setFileSizeBytes(bytes == null ? file.getSize() : bytes.longValue());
        media.setWidth(width == null ? null : width.intValue());
        media.setHeight(height == null ? null : height.intValue());
        return media;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select an image to upload.");
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                    "Image must be 10 MB or smaller.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only JPEG, PNG and WEBP images are supported.");
        }
    }

    private MedialResponse toResponse(Media media) {
        return new MedialResponse(media.getId(),
                                  media.getUserId(),
                                  media.getMediaCategory(),
                                  media.getImageUrl(),
                                  media.getFormat(),
                                  media.getFileSizeBytes(),
                                  media.getWidth(),
                                  media.getHeight(),
                                  media.getCreatedAt());
    }
}

