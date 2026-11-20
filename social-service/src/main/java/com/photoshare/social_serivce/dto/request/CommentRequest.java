package com.photoshare.social_serivce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {

    @NotBlank
    @Size(max=2000)
    public String content;

}
