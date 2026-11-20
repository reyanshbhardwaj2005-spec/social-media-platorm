package com.photoshare.social_serivce.repository;

import com.photoshare.social_serivce.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostLikeRepository extends JpaRepository<PostLike, UUID> {

    boolean existsByUserIdAndPostId(UUID current, UUID postId);

    void deleteByUserIdAndPostId(UUID current, UUID postId);

    long countByPostId(UUID postId);

}