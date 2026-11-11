package com.photoshare.user_service.repository;

import com.photoshare.user_service.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.net.ContentHandler;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    boolean existsByUserId(UUID uuid);

    UserProfile findByUserId(UUID uuid);

    @Query("""
    SELECT p FROM UserProfile p
    WHERE p.userId <> :currentUserId
      AND (
          LOWER(p.username) LIKE LOWER(CONCAT('%', :query, '%'))
          OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
          OR LOWER(COALESCE(p.bio, '')) LIKE LOWER(CONCAT('%', :query, '%'))
      )
    ORDER BY p.createdAt DESC
    """)
    Page<UserProfile> searchProfiles(
            @Param("query") String query,
            @Param("currentUserId") UUID currentUserId,
            Pageable pageable
    );
}
