package com.photoshare.social_serivce.service;

import com.photoshare.social_serivce.client.PostServiceRestClient;
import com.photoshare.social_serivce.entity.Follow;
import com.photoshare.social_serivce.repository.FollowRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FeedService {

    private final FollowRepository followRepository;
    private final PostServiceRestClient postServiceClient;

    public FeedService(FollowRepository followRepository, PostServiceRestClient postServiceClient) {
        this.followRepository = followRepository;
        this.postServiceClient = postServiceClient;
    }

    public Map<String, Object> getFeed(UUID uuid, int page, int size, String accessToken) {

        if(page<0||size<1||size>100) {
            throw new IllegalArgumentException("Invalid pagination values.");
        }

        List<UUID> following = new ArrayList<>();

        Page<Follow> relationships = followRepository.findAllByFollowerId(uuid, PageRequest.of(0,10000));
        relationships.getContent().forEach(f->following.add(f.getFollowingId()));

        if(following.isEmpty()) {
            return Map.of("posts",List.of(),"page",page,"size",size,"totalElements",0,"totalPages",0,"last",true);
        }

        return postServiceClient.getFeed(following, page, size, accessToken);
    }
}
