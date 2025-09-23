package com.hotketok.dto;

public record SignUpRequest (
        String logInId,
        String password,
        String phoneNumber,
        String name
){
}
