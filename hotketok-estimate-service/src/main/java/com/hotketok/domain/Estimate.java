package com.hotketok.domain;

import com.hotketok.domain.enums.Category;
import com.hotketok.domain.enums.Status;
import com.hotketok.exception.EstimateErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "estimate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Estimate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long requestId;

    @Column(nullable = false)
    private Long vendorId;

    @Enumerated(EnumType.STRING)
    private Status status;

    // decisionLater가 true인 경우에만 null 허용
    private BigDecimal estimatePrice;

    private Boolean decisionLater;

    @Column(length = 1000, nullable = false)
    private String comment;

    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Estimate(Long requestId, Long vendorId, BigDecimal estimatePrice, Boolean decisionLater, String comment) {
        this.requestId = requestId;
        this.vendorId = vendorId;
        this.estimatePrice = estimatePrice;
        this.decisionLater = (decisionLater != null) && decisionLater;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    public static Estimate createEstimate(Long requestId, Long vendorId, BigDecimal estimatePrice, Boolean decisionLater, String comment) {

        if (Boolean.FALSE.equals(decisionLater) && estimatePrice == null) {
            throw new CustomException(EstimateErrorCode.PRICE_IS_ESSENTIAL);
        }

        return Estimate.builder()
                .requestId(requestId)
                .vendorId(vendorId)
                .estimatePrice(estimatePrice)
                .decisionLater(decisionLater)
                .comment(comment)
                .build();
    }
}
