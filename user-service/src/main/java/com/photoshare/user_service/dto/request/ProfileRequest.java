package com.photoshare.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

//    @Size(max = 1000, message = "Profile picture URL must not exceed 1000 characters")
//    private String profilePictureUrl;

    @Size(max = 500, message = "Website must not exceed 500 characters")
    private String website;

    private Boolean privateAccount;
}
