package com.photoshare.media_serivce.repository;

import com.photoshare.media_serivce.entity.Media;
import com.photoshare.media_serivce.enums.MediaCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaRepository extends JpaRepository<Media, UUID> {

    Optional<Media> findByIdAndUserId(UUID mediaId, UUID userId);

    Optional<Media> findFirstByUserIdAndMediaCategory(UUID userId, MediaCategory mediaCategory);

    List<Media> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Media> findAllByUserIdAndMediaCategoryOrderByCreatedAtDesc(UUID userId, MediaCategory category);

}
