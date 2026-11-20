package com.photoshare.social_serivce.repository;

import com.photoshare.social_serivce.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Page<Comment> findAllByPostIdOrderByCreatedAtDesc(UUID postId, PageRequest of);

    long countByPostId(UUID postId);
}