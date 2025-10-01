package com.hotketok.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Notice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(nullable = false)
    private String address; // 공지사항이 등록된 주택의 주소

    private Long authorId;  // 공지사항 작성자 ID

    @Column(columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private Boolean isFix;

    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PROTECTED)
    private Notice(String address, Long authorId, String title, String content, Boolean isFix, LocalDateTime created_at) {
        this.address = address;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.isFix = isFix;
        this.createdAt = LocalDateTime.now();
    }

    public static Notice createNotice(String address, Long authorId, String title, String content, Boolean isFix, LocalDateTime created_at) {
        return Notice.builder()
                .address(address)
                .authorId(authorId)
                .title(title)
                .content(content)
                .isFix(isFix != null && isFix)
                .build();
    }
}
