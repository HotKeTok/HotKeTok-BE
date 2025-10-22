package com.hotketok.dto;

import java.util.List;

public record MyPageInfoResponse (
        String profileImage,
        String name,
        String phoneNumber,
        String logInId,
        String address,
        String number,
        List<String> houseTags
){
}
