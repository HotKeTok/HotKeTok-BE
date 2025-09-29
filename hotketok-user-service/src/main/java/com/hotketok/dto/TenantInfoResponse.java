package com.hotketok.dto;

import com.hotketok.domain.User;

public record TenantInfoResponse (
        String name,
        String phoneNumber,
        String profileImageUrl
){
    public static TenantInfoResponse of(User user) {
        return new TenantInfoResponse(user.getName(),user.getPhoneNumber(),user.getProfileImage());
    }
}
