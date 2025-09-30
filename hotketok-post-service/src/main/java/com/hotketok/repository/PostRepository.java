package com.hotketok.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hotketok.domain.Post;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByReceiverId(Long recipientId);
    List<Post> findBySenderId(Long senderId);
    List<Post> findAllBySenderIdIn(List<Long> senderIds);
}
