package com.hotketok.repository;

import com.hotketok.domain.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    Optional<PostTag> findByContent(String content);

    // 다중 태그로 찾음
    Set<PostTag> findByContentIn(List<String> contents);
}
