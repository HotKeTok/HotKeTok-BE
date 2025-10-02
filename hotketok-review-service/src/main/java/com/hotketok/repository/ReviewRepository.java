package com.hotketok.repository;
import com.hotketok.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReviewRepository extends JpaRepository<Review, Long> {}