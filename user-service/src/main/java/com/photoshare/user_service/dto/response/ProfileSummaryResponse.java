package com.photoshare.user_service.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class ProfileSummaryResponse {

    private String username;
    private UUID userId;
    private String fullName;
    private String profilePictureUrl;
    private boolean privateAccount;
}
