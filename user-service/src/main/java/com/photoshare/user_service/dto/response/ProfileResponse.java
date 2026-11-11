package com.photoshare.user_service.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProfileResponse {

    private UUID profileId;
    private UUID userId;
    private String username;
    private String fullName;
    private String bio;
    private String profilePictureUrl;
    private String website;
    private boolean privateAccount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
