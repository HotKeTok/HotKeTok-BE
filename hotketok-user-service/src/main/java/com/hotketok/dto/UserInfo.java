package com.hotketok.dto;

import com.hotketok.domain.enums.Role;

public record UserInfo (
        Long id,
        String logInId,
        String password,
        Role role,
        boolean onBoardingStageFlag
){
    public static UserInfo of(Long id, String logInId, String password, Role role, boolean onBoardingStageFlag) {
        return new UserInfo(id, logInId, password, role, onBoardingStageFlag);
    }
}
