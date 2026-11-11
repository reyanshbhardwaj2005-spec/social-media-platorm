package com.photoshare.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshRequest {

    @NotBlank
    public String refreshToken;
}
