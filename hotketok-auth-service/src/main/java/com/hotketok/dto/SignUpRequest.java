package com.hotketok.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest (
        @NotBlank String name,

        @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$",
                message = "아이디는 6~20자의 영문/숫자만 가능합니다.")
        String logInId,

        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[~,@,$,^,*,(,),_,+]).{9,16}$",
                message = "비밀번호는 영문, 숫자, 특수문자 포함 9~16자여야 합니다.")
        String password,

        @Pattern(regexp = "^010\\d{4}\\d{4}$",
                message = "휴대폰 번호는 010xxxxxxxx 형식이어야 합니다.")
        String phoneNumber,

        @Email String email
){
}
