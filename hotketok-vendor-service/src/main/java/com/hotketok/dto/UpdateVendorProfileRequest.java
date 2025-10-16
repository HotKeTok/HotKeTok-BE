package com.hotketok.dto;

import java.util.List;

public record UpdateVendorProfileRequest(
        String introduction,
        String phoneNumber,
        RunningTimeRequest runningTime,
        String profileImage,
        List<String> introductionImages
) {}