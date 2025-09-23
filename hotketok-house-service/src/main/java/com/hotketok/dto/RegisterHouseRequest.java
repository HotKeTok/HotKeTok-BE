package com.hotketok.dto;

public record RegisterHouseRequest (
        String address,
        String detailAddress,
        String floor,
        String number
){}
