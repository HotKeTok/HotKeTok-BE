package com.hotketok.dto;

public record PatchNewsRequest (
        Long newsId,
        String title,
        String content
) {}
