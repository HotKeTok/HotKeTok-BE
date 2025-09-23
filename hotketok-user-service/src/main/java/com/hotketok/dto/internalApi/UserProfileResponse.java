package com.hotketok.dto.internalApi;

import com.hotketok.domain.User;
import com.hotketok.domain.enums.Role;
import com.hotketok.domain.enums.SenderType;

import static com.hotketok.domain.enums.SenderType.HOUSE_USER;


public record UserProfileResponse(
        Long userId,
        String userName,
        String profileImageUrl,
        SenderType role // 채팅 서비스의 SenderType
) {

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getProfileImage(),
                convertRoleToSenderType(user.getRole())  // ENUM 타입 반환
        );
    }

    // 유저 서비스의 Role -> 채팅 서비스의 UserType으로 변환
    private static SenderType convertRoleToSenderType(Role role) {
        if (role == null) {
            return HOUSE_USER; // 기본값
        }
        return switch (role.name()) {
            case "VENDOR" -> SenderType.VENDOR;
            case "TENANT" -> SenderType.TENANT;
//            case "OWNER" -> HOUSE_USER;
            default -> HOUSE_USER;
        };
    }
}

