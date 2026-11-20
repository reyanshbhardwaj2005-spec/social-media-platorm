package com.photoshare.social_serivce.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    public UUID id;
    public UUID userId;
    public UUID postId;
    public String content;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

}
