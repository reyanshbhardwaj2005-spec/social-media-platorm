package com.photoshare.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "user_profiles",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_profiles_user_id",
                columnNames = "user_id"
        ),
        indexes = {
                @Index(name = "idx_user_profiles_full_name", columnList = "full_name")
        }
)
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // This is the UUID from auth-service. It is a logical reference, not a cross-DB FK.
    @Column(name = "user_id", nullable = false, unique = true, updatable = false)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(length = 500)
    private String bio;

    // Logical reference to the media record owned by Media Service.
    @Column(name = "profile_picture_media_id")
    private UUID profilePictureMediaId;

    @Column(name = "profile_picture_url", length = 1000)
    private String profilePictureUrl;

    @Column(length = 500)
    private String website;

    @Column(name = "private_account", nullable = false)
    private boolean privateAccount = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }
}
