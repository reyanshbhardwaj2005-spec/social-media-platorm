package com.photoshare.post_serivce.repository;

import com.photoshare.post_serivce.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    Page<Post> findAllByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Post> findAllByUserIdInOrderByCreatedAtDesc(List<UUID> userIds, Pageable pageable);
}
