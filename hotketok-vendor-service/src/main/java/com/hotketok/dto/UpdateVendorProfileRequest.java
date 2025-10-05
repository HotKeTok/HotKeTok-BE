package com.hotketok.dto;

public record UpdateVendorProfileRequest(
        String introduction,
        String phoneNumber,
        String runningTime,
        String profileImage
) {}