package com.photoshare.social_serivce.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LikeStatusResponse {

    public UUID postId;
    public boolean likedByMe;
    public long likeCount;

}
