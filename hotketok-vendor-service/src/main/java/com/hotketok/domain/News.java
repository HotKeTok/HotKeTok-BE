package com.hotketok.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@Getter
@NoArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

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
