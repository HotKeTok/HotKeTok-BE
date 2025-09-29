package com.hotketok.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostToTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_to_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id") // DB에 생성될 외래키 컬럼명
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posttag_id") // DB에 생성될 외래키 컬럼명
    private PostTag tag;

    public static PostToTag createPostToTag(Post post, PostTag tag) {
        PostToTag postToTag = new PostToTag();
        postToTag.post = post;
        postToTag.tag = tag;
        return postToTag;
    }
}