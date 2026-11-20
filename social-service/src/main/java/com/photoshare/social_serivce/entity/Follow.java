package com.photoshare.social_serivce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="follows",
        uniqueConstraints = @UniqueConstraint(name="uk_follower_following", columnNames={"follower_id","following_id"}), indexes = {
        @Index(name="idx_follower_id",columnList="follower_id"),
        @Index(name="idx_following_id",columnList="following_id")
})

public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "follower_id", nullable = false, updatable = false)
    private UUID followerId;

    @Column(name = "following_id", nullable = false, updatable = false)
    private UUID followingId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Follow(UUID me, UUID target) {
        this.followerId = me;
        this.followingId = target;
    }
}