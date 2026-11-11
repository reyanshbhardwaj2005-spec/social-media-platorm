package com.photoshare.auth_service.dto.response;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    public String tokenType;
    public String accessToken;
    public String refreshToken;
    public long accessTokenExpiresInMs;
    public UserResponse user;
}
