package com.hotketok.dto;

import com.hotketok.domain.Role;

public record UserInfo (
     Long id,
     String logInId,
     String password,
     Role role
){}
