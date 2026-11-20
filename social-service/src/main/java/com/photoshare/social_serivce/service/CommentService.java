package com.photoshare.social_serivce.service;

import com.photoshare.social_serivce.client.PostServiceRestClient;
import com.photoshare.social_serivce.dto.request.CommentRequest;
import com.photoshare.social_serivce.dto.response.CommentResponse;
import com.photoshare.social_serivce.dto.response.CountResponse;
import com.photoshare.social_serivce.dto.response.PageResponse;
import com.photoshare.social_serivce.entity.Comment;
import com.photoshare.social_serivce.exception.ResourceNotFoundException;
import com.photoshare.social_serivce.repository.CommentRepository;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository repository;
    private final PostServiceRestClient postServiceClient;

    public CommentService(CommentRepository repository, PostServiceRestClient postServiceClient) {
        this.repository = repository;
        this.postServiceClient = postServiceClient;
    }

    @Transactional
    public @Nullable CommentResponse create(UUID current, UUID postId, @Valid CommentRequest request) {
        postServiceClient.requirePost(postId);
        return toResponse(repository.save(new Comment(current,postId,request.content.trim())));
    }

    @Transactional
    public CommentResponse update(UUID current, UUID commentId, @Valid CommentRequest request) {
        Comment c=repository.findById(commentId).orElseThrow(()->new ResourceNotFoundException("Comment not found."));
        if(!c.getUserId().equals(current)) throw new BadRequestException("You can only edit your own comments.");
        c.updateContent(request.content.trim());return toResponse(repository.save(c));
    }

    @Transactional
    public void delete(UUID current, UUID commentId) {
        Comment c=repository.findById(commentId).orElseThrow(()->new ResourceNotFoundException("Comment not found."));
        if(!c.getUserId().equals(current)) throw new BadRequestException("You can only delete your own comments.");
        repository.delete(c);
    }

    public PageResponse<CommentResponse> getByPost(UUID postId,int page,int size) {

        if(page<0||size<1||size>100) {
            throw new BadRequestException("Page must be >= 0 and size must be between 1 and 100.");
        }

        Page<CommentResponse> result = repository.findAllByPostIdOrderByCreatedAtDesc(postId,PageRequest.of(page,size)).map(this::toResponse);

        return PageResponse.from(result);
    }


    public CountResponse count(UUID postId) {
        return new CountResponse(repository.countByPostId(postId));
    }

    private CommentResponse toResponse(Comment c) {
        return new CommentResponse(c.getId(),
                c.getUserId(),
                c.getPostId(),
                c.getContent(),
                c.getCreatedAt(),
                c.getUpdatedAt());
    }

}
