package com.hotketok.dto;

import com.hotketok.domain.Role;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
        @NotBlank
        String logInId,
        @NotBlank
        String password,
        @NotBlank
        Role role
){}
