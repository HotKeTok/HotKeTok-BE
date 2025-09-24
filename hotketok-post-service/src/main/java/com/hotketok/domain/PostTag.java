package com.hotketok.domain;

import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "posttag_id")
    private Long id;

    @Column(name = "posttag_content")
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private PostTag(String content) {this.content = content;}

    public static PostTag createPostTag(String content) {
        return PostTag.builder()
                .content(content)
                .build();
    }
}
