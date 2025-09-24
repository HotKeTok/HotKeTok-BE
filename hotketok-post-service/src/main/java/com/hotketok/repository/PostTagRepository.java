package com.hotketok.repository;

import com.hotketok.domain.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    Optional<PostTag> findByContent(String content);
}
