package com.photoshare.post_serivce.service;

import com.photoshare.post_serivce.client.MediaServiceClient;
import com.photoshare.post_serivce.dto.media.MediaUploadResponse;
import com.photoshare.post_serivce.dto.request.UpdatePostRequest;
import com.photoshare.post_serivce.dto.response.PostPageResponse;
import com.photoshare.post_serivce.dto.response.PostResponse;
import com.photoshare.post_serivce.entity.Post;
import com.photoshare.post_serivce.repository.PostRepository;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PostService {

    private final MediaServiceClient mediaServiceClient;
    private final PostRepository postRepository;

    public PostService(MediaServiceClient mediaServiceClient, PostRepository postRepository) {
        this.mediaServiceClient = mediaServiceClient;
        this.postRepository = postRepository;
    }

    public @Nullable PostResponse createPost(UUID userId, String caption, MultipartFile image, String accessToken) {
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post image is required.");
        }

        MediaUploadResponse media = mediaServiceClient.uploadPostImage(image, userId, accessToken);

        try {
            Post post = new Post();
            post.setUserId(userId);
            post.setMediaId(media.getId());
            post.setImageUrl(media.getImageUrl());
            post.setCaption(normalizeCaption(caption));

            return toResponse(postRepository.save(post));
        } catch (RuntimeException ex) {

            try {
                mediaServiceClient.deleteMedia(media.getId(), accessToken);
            } catch (RuntimeException cleanupException) {
                ex.addSuppressed(cleanupException);
            }
            throw ex;
        }
    }

    public @Nullable PostResponse getPost(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("No post with this id exists."));
        return toResponse(post);

    }

    public @Nullable PostPageResponse getMyPosts(UUID userId, int page, int size) {
        return toPageResponse(findUserPosts(userId, page, size));
    }

    public @Nullable PostPageResponse getPostsByUser(UUID userId, int page, int size) {
        return toPageResponse(findUserPosts(userId, page, size));
    }

    public @Nullable PostResponse updatePost(UUID uuid, UUID postId, @Valid UpdatePostRequest request) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("No post with this id exists."));
        verifyOwnership(post, uuid);

        post.setCaption(normalizeCaption(request.getCaption()));
        return toResponse(postRepository.save(post));
    }

    @Transactional
    public void deletePost(UUID uuid, UUID postId, String accessToken) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("No post with this id exists."));
        verifyOwnership(post, uuid);

        // Delete media first. If Media Service fails, retain the post row.
        mediaServiceClient.deleteMedia(post.getMediaId(), accessToken);
        postRepository.delete(post);
    }


    private Page<Post> findUserPosts(UUID userId, int page, int size) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must be zero or greater.");
        }
        if (size < 1 || size > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must be between 1 and 50.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    private void verifyOwnership(Post post, UUID userId) {
        if (!post.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to modify this post.");
        }
    }

    private String normalizeCaption(String caption) {
        if (!StringUtils.hasText(caption)) {
            return null;
        }
        String normalized = caption.trim();
        if (normalized.length() > 2200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Caption cannot exceed 2200 characters.");
        }
        return normalized;
    }
    private PostPageResponse toPageResponse(Page<Post> page) {
        PostPageResponse response = new PostPageResponse();
        response.setContent(page.getContent().stream().map(this::toResponse).toList());
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        return response;
    }

    private PostResponse toResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setUserId(post.getUserId());
        response.setMediaId(post.getMediaId());
        response.setImageUrl(post.getImageUrl());
        response.setCaption(post.getCaption());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        return response;
    }
}
