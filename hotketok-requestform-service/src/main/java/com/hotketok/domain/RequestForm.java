package com.hotketok.domain;

import com.hotketok.domain.enums.Category;
import com.hotketok.domain.enums.PayType;
import com.hotketok.domain.enums.Status;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_forms")
@Getter
@NoArgsConstructor
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

}
