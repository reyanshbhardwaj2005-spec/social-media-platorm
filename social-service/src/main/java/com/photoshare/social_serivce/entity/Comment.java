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
@Table(name="comments", indexes = {
        @Index(name="idx_comment_post_created", columnList="post_id,created_at"),
        @Index(name="idx_comment_user",columnList="user_id")
})

public class Comment {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(name="user_id",nullable=false,updatable=false)
    private UUID userId;

    @Column(name="post_id",nullable=false,updatable=false)
    private UUID postId;

    @Column(nullable=false,length=2000)
    private String content;

    @Column(name="created_at",nullable=false,updatable=false)
    private LocalDateTime createdAt;

    @Column(name="updated_at",nullable=false)
    private LocalDateTime updatedAt;

    public Comment(UUID current, UUID postId, String trim) {
        this.userId = current;
        this.postId = postId;
        this.content = trim;
    }

    @PrePersist void onCreate(){
        createdAt=LocalDateTime.now();updatedAt=createdAt;
    }

    @PreUpdate void onUpdate(){
        updatedAt=LocalDateTime.now();
    }

    public void updateContent(String content){
        this.content=content;
    }
}
