package com.hotketok.domain;


import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
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
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "post_to_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "posttag_id"))
    private Set<PostTag> tags = new HashSet<>();

    @Builder
    private Post(Long receiverId, Long senderId, String content, Boolean isAnonymous, Set<PostTag> tags) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.tags = tags;
    }
}
