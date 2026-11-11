package com.photoshare.user_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrivacyRequest {

    @NotNull(message = "privateAccount is required")
    private Boolean privateAccount;
}
