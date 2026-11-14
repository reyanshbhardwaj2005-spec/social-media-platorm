package com.photoshare.media_serivce.entity;

import com.photoshare.media_serivce.enums.MediaCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "media", indexes = {
        @Index(name = "idx_media_user_id", columnList = "user_id"),
        @Index(name = "idx_media_created_at", columnList = "created_at")
})

public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_category", nullable = false, length = 30)
    private MediaCategory mediaCategory;

    @Column(name = "cloudinary_public_id", nullable = false, unique = true, length = 500)
    private String cloudinaryPublicId;

    @Column(name = "image_url", nullable = false, length = 2000)
    private String imageUrl;

    @Column(nullable = false, length = 30)
    private String format;

    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @Column(name = "width_px")
    private Integer width;

    @Column(name = "height_px")
    private Integer height;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
