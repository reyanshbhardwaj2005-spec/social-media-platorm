package com.photoshare.post_serivce.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequest {

    @Size(max = 2200, message = "Caption cannot exceed 2200 characters.")
    private String caption;
}
