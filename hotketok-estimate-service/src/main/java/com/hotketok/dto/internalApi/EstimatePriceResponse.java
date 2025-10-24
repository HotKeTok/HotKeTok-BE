package com.hotketok.dto.internalApi;

import java.math.BigDecimal;

public record EstimatePriceResponse(
        BigDecimal estimatePrice
) {
}
