package com.photoshare.social_serivce.repository;

import com.photoshare.social_serivce.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {

    Page<Follow> findAllByFollowerId(UUID uuid, PageRequest of);

    boolean existsByFollowerIdAndFollowingId(UUID me, UUID target);

    void deleteByFollowerIdAndFollowingId(UUID me, UUID target);

    Page<Follow> findAllByFollowingId(UUID userId, Pageable pageable);

    long countByFollowingId(UUID target);

    long countByFollowerId(UUID target);
}