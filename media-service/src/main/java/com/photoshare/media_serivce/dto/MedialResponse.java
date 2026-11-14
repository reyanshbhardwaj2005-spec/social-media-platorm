package com.photoshare.media_serivce.dto;

import com.photoshare.media_serivce.enums.MediaCategory;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MedialResponse {

    private UUID mediaId;
    private UUID userId;
    private MediaCategory mediaCategory;
    private String imageUrl;
    private String format;
    private Long fileSizeBytes;
    private Integer width;
    private Integer height;
    private LocalDateTime createdAt;
}
