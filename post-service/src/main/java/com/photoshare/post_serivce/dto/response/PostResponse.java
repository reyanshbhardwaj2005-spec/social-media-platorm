package com.photoshare.post_serivce.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private UUID id;
    private UUID userId;
    private UUID mediaId;
    private String imageUrl;
    private String caption;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
