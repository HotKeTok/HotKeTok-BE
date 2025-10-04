package com.hotketok.domain;

import com.hotketok.domain.enums.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
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

    @Column(nullable = false)
    private DecimalFormat estimatePrice;

    @Column(nullable = false)
    private LocalDateTime estimateTime;

    @Column(length = 1000, nullable = false)
    private String comment;
}
