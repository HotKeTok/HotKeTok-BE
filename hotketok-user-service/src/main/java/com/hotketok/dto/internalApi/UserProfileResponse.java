package com.hotketok.dto.internalApi;

import com.hotketok.domain.User;
import com.hotketok.domain.enums.Role;

public record UserProfileResponse(
        Long userId,
        String userName,
        String profileImageUrl,
        Role role
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getProfileImage(),
                user.getRole()
        );
    }
}