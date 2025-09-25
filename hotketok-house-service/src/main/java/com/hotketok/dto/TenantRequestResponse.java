package com.hotketok.dto;

import com.hotketok.dto.internalApi.TenantInfoResponse;

public record TenantRequestResponse (
        Long houseId,
        String name,
        String phoneNumber,
        String profileImageUrl,
        String houseNumber
){
    public static TenantRequestResponse of(Long houseId,TenantInfoResponse response, String houseNumber) {
        return new TenantRequestResponse(houseId,response.name(),response.phoneNumber(),response.profileImageUrl(),houseNumber);
    }
}
