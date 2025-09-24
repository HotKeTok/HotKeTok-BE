package com.hotketok.domain;


import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post  extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(length = 1000, nullable = false)
    private String content;

    @Column(name = "isAnonymous", nullable = true)
    private Boolean isAnonymous;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posttag_id")
    private PostTag postTag;

    @Builder
    private Post(Long receiverId, Long senderId, String content, Boolean isAnonymous, PostTag postTag) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.postTag = postTag;
    }
}
