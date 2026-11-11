package com.photoshare.auth_service.dto.response;

import com.photoshare.auth_service.entity.User;
import com.photoshare.auth_service.enums.UserRole;
import lombok.*;

import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private UUID id;
    private String username;
    private String email;
    private UserRole userRole;

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
}
