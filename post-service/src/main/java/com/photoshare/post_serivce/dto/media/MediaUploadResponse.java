package com.photoshare.post_serivce.dto.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MediaUploadResponse {

    @JsonProperty("mediaId")
    private UUID id;

    private UUID userId;
    private String mediaCategory;
    private String imageUrl;
    private String format;
    private Long fileSizeBytes;
    private Integer width;
    private Integer height;
    private LocalDateTime createdAt;
}
