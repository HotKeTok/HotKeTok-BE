package com.hotketok.domain;

import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "request_form_images")
@Getter
@NoArgsConstructor
public class RequestFormImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_form_id")
    private RequestForm requestForm;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Builder(access = AccessLevel.PRIVATE)
    public RequestFormImage(RequestForm requestForm, String imageUrl) {
        this.requestForm = requestForm;
        this.imageUrl = imageUrl;
    }

    public static RequestFormImage createRequestFormImage(RequestForm requestForm, String imageUrl) {
        return new RequestFormImage(requestForm, imageUrl);
    }
}
