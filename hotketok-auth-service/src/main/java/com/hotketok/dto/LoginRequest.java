package com.hotketok.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
        @NotBlank
        String logInId,
        @NotBlank
        String password
){}
