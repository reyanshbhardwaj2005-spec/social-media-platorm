package com.photoshare.user_service.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class MediaUploadResponse {

    private UUID mediaId;
    private UUID userId;
    private String mediaCategory;
    private String imageUrl;
    private String format;
    private Long fileSizeBytes;
    private Integer width;
    private Integer height;
}
