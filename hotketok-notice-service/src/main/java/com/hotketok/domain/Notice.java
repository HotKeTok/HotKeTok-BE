package com.hotketok.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "houses")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long houseId;

    private Long authorId;  // 공지사항 작성자 ID

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private Boolean isFix;

    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Notice(Long houseId, Long authorId, String title, String content, Boolean isFix, LocalDateTime created_at) {
        this.houseId = houseId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.isFix = isFix;
        this.createdAt = created_at
    }

    public static Notice createNotice(Long houseId, Long authorId, String title, String content, Boolean isFix, LocalDateTime created_at) {
        return Notice.builder()
                .houseId(houseId)
                .authorId(authorId)
                .title(title)
                .content(content)
                .isFix(false)
                .created_at(created_at)
                .build();
    }
}
