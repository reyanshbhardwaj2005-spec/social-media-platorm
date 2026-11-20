package com.photoshare.social_serivce.entity;

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
@Table(name="post_likes",
        uniqueConstraints = @UniqueConstraint(name="uk_user_post_like",columnNames={"user_id","post_id"}), indexes = {
        @Index(name="idx_like_post_id",columnList="post_id"),
        @Index(name="idx_like_user_id",columnList="user_id")
})

public class PostLike {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(name="user_id",nullable=false,updatable=false)
    private UUID userId;

    @Column(name="post_id",nullable=false,updatable=false)
    private UUID postId;

    @Column(name="created_at",nullable=false,updatable=false)
    private LocalDateTime createdAt;

    public PostLike(UUID userId,UUID postId) {
        this.userId=userId;this.postId=postId;
    }

    @PrePersist
    void onCreate() {
        createdAt=LocalDateTime.now();
    }
}
