package com.hotketok.domain;


import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    @Column(name = "silent_time")
    private String silentTime;

    // 태그 다중 선택 가능
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostToTag> postToTags = new HashSet<>();

    @Builder
    private Post(Long receiverId, Long senderId, String content, Boolean isAnonymous, String silentTime) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.silentTime = silentTime;
    }

    // 연관관계 편의 메서드
    public void addTag(PostTag tag) {
        PostToTag postToTag = PostToTag.createPostToTag(this, tag);
        this.postToTags.add(postToTag);
    }
}
