package com.photoshare.social_serivce.service;

import com.photoshare.social_serivce.client.UserServiceRestClient;
import com.photoshare.social_serivce.dto.response.CountResponse;
import com.photoshare.social_serivce.dto.response.FollowResponse;
import com.photoshare.social_serivce.dto.response.FollowStatusResponse;
import com.photoshare.social_serivce.dto.response.PageResponse;
import com.photoshare.social_serivce.entity.Follow;
import com.photoshare.social_serivce.exception.ResourceNotFoundException;
import com.photoshare.social_serivce.repository.FollowRepository;
import jakarta.ws.rs.BadRequestException;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FollowService {

    private final FollowRepository repository;
    private final UserServiceRestClient userServiceClient;

    public FollowService(FollowRepository repository, UserServiceRestClient userServiceClient) {
        this.repository = repository;
        this.userServiceClient = userServiceClient;
    }

    @Transactional
    public @Nullable FollowResponse follow(UUID me, UUID target) {

        if(me.equals(target)) {
            throw new BadRequestException("You cannot follow yourself.");
        }

        userServiceClient.requireUser(target);
        if(repository.existsByFollowerIdAndFollowingId(me,target)) throw new BadRequestException("You are already following this user.");
        Follow f = repository.save(new Follow(me,target));
        return new FollowResponse(f.getId(),f.getFollowerId(),f.getFollowingId(),f.getCreatedAt());
    }

    @Transactional
    public void unfollow(UUID me, UUID target) {

        if(!repository.existsByFollowerIdAndFollowingId(me,target)) {
            throw new ResourceNotFoundException("Follow relationship not found.");
        }
        repository.deleteByFollowerIdAndFollowingId(me,target);
    }

    public PageResponse<FollowResponse> followers(UUID userId, int page, int size) {
        return PageResponse.from(repository.findAllByFollowingId(userId,pageable(page,size)).map(this::toResponse));
    }

    public PageResponse<FollowResponse> following(UUID userId, int page, int size) {
        return PageResponse.from(repository.findAllByFollowerId(userId, (PageRequest) pageable(page,size)).map(this::toResponse));

    }

    public FollowStatusResponse status(UUID me, UUID target) {
        return new FollowStatusResponse(target,repository.existsByFollowerIdAndFollowingId(me,target),
                repository.existsByFollowerIdAndFollowingId(target,me),
                repository.countByFollowingId(target),
                repository.countByFollowerId(target));
    }

    public CountResponse followerCount(UUID userId) {
        return new CountResponse(repository.countByFollowingId(userId));
    }

    public CountResponse followingCount(UUID userId) {
        return new CountResponse(repository.countByFollowerId(userId));
    }

    private Pageable pageable(int page, int size) {
        if(page<0||size<1||size>100) {
            throw new BadRequestException("Page must be >= 0 and size must be between 1 and 100.");
        }
        return PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"createdAt"));
    }

    private FollowResponse toResponse(Follow f) {
        return new FollowResponse(f.getId(),f.getFollowerId(),f.getFollowingId(),f.getCreatedAt());
    }
}
