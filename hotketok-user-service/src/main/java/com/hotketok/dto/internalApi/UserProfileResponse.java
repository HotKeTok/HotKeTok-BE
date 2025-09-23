package com.hotketok.dto.internalApi;

import com.hotketok.domain.User;
import com.hotketok.domain.enums.Role;

public record UserProfileResponse(
        Long userId,
        String userName,
        String profileImageUrl,
        String role
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getProfileImage(),
                convertRoleToString(user.getRole())
        );
    }

    // 유저 서비스 role을 string으로 변환
    private static String convertRoleToString(Role role) {
        if (role == null) {
            return "HOUSE_USER";
        }
        return switch (role.name()) {
            case "VENDOR" -> "VENDOR";
            case "TENANT" -> "TENANT";
//            case "OWNER" -> "OWNER";
            default -> "OWNER";
        };
    }
}