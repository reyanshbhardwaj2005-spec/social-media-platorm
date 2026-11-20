package com.photoshare.social_serivce.service;

import com.photoshare.social_serivce.client.PostServiceRestClient;
import com.photoshare.social_serivce.dto.response.CountResponse;
import com.photoshare.social_serivce.dto.response.LikeStatusResponse;
import com.photoshare.social_serivce.entity.PostLike;
import com.photoshare.social_serivce.exception.ResourceNotFoundException;
import com.photoshare.social_serivce.repository.PostLikeRepository;
import jakarta.ws.rs.BadRequestException;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LikeService {

    private final PostLikeRepository repository;
    private final PostServiceRestClient postServiceClient;

    public LikeService(PostLikeRepository repository, PostServiceRestClient postServiceClient) {
        this.repository = repository;
        this.postServiceClient = postServiceClient;
    }

    public @Nullable LikeStatusResponse like(UUID current, UUID postId) {
        postServiceClient.requirePost(postId);
        if(repository.existsByUserIdAndPostId(current,postId)) throw new BadRequestException("You have already liked this post.");
        repository.save(new PostLike(current,postId));
        return status(current,postId);
    }

    @Transactional
    public void unlike(UUID current, UUID postId) {
        if(!repository.existsByUserIdAndPostId(current,postId)) throw new ResourceNotFoundException("Like not found.");
        repository.deleteByUserIdAndPostId(current,postId);
    }

    public LikeStatusResponse status(UUID current, UUID postId) {
        return new LikeStatusResponse(postId,repository.existsByUserIdAndPostId(current,postId),repository.countByPostId(postId));
    }

    public CountResponse count(UUID postId) {
        return new CountResponse(repository.countByPostId(postId));
    }
}
