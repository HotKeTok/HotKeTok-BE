package com.hotketok.dto;

import java.math.BigDecimal;

public record PostEstimateRequest(

        Long requestFormId,
        BigDecimal estimatePrice,
        Boolean decisionLater,
        String comment
) {}
