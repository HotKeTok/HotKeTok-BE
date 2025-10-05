package com.hotketok.dto;

import java.util.List;

public record UpdateVendorProfileRequest(
        String introduction,
        String phoneNumber,
        String runningTime,
        String profileImage,
        List<String> introductionImages // 소개 사진 목록
) {}