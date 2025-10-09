package com.hotketok.dto.internalApi;
import com.hotketok.domain.User;

public record UserInfoDetailResponse(
        Long userId,
        String name,
        String phoneNumber
) {
    public static UserInfoDetailResponse from(User user) {
        return new UserInfoDetailResponse(user.getId(), user.getName(), user.getPhoneNumber());
    }
}