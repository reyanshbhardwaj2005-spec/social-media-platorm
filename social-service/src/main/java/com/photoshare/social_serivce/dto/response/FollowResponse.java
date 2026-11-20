package com.photoshare.social_serivce.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowResponse {

    public UUID id;
    public UUID followerId;
    public UUID followingId;
    public LocalDateTime createdAt;
}
