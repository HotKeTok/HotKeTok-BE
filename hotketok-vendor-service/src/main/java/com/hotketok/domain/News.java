package com.hotketok.domain;

import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@Getter
@NoArgsConstructor
public class News extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String content;

    @Builder(access = AccessLevel.PROTECTED)
    private News(Vendor vendor, String title, String content) {
        this.vendor = vendor;
        this.title = title;
        this.content = content;
    }

    public static News createNews(String title, String content) {
        return News.builder()
                .title(title)
                .content(content)
                .build();
    }

    void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
}
