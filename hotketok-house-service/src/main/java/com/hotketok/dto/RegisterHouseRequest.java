package com.hotketok.dto;


public record RegisterHouseRequest (
        String address,
        String detailAddress,
        Integer count
){
}
