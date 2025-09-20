package com.hotketok.domain;

import com.hotketok.domain.enums.Category;
import com.hotketok.domain.enums.PayType;
import com.hotketok.domain.enums.Status;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_forms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestForm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false)
    private Long payerId;

    @Enumerated(EnumType.STRING)
    private PayType payType; // 결제 타입

    @Column(nullable = false, length = 300)
    private String description;

    @Column(nullable = false)
    private LocalDateTime requestSchedule;

    @Enumerated(EnumType.STRING)
    private Category category; // 수리 종류

    @Enumerated(EnumType.STRING)
    private Status status; // 요청서 상태


    @Builder(access = AccessLevel.PRIVATE)
    private RequestForm(
            Long authorId,
            Long payerId,
            PayType payType,
            String description,
            LocalDateTime requestSchedule,
            Category category,
            Status status){
        this.authorId = authorId;
        this.payerId = payerId;
        this.payType = payType;
        this.description = description;
        this.requestSchedule = requestSchedule;
        this.category = category;
        this.status = status;
    }

    public static RequestForm createRequestForm(
            Long authorId,
            Long payerId,
            PayType payType,
            String description,
            LocalDateTime requestSchedule,
            Category category,
            Status status
            ) {
        return RequestForm.builder()
                .authorId(authorId)
                .payerId(payerId)
                .payType(payType)
                .description(description)
                .requestSchedule(requestSchedule)
                .category(category)
                .status(status)
                .build();
    }
}
