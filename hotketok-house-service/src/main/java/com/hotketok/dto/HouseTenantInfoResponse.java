package com.hotketok.dto;

import com.hotketok.dto.internalApi.TenantInfoResponse;

public record HouseTenantInfoResponse(
        TenantInfoResponse tenantInfo,
        String houseMemo
) {
}
