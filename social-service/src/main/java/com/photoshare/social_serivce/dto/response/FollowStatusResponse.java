package com.photoshare.social_serivce.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowStatusResponse {

    public UUID userId;
    public boolean following;
    public boolean followedByMe;
    public long followerCount;
    public long followingCount;
}
