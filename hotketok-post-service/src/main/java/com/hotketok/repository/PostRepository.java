package com.hotketok.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hotketok.domain.Post;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByReceiverId(Long recipientId);
    List<Post> findBySenderId(Long senderId);
    List<Post> findAllBySenderIdIn(List<Long> senderIds);
    @Override
    @EntityGraph(attributePaths = {"postToTags", "postToTags.tag"})
    Optional<Post> findById(Long postId);
}
