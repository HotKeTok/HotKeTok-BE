package com.hotketok.repository;

import com.hotketok.domain.RequestFormImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestFormImageRepository extends JpaRepository<RequestFormImage, Long> {
    void deleteAllByIdIn(List<Long> RequestFormImageIds);
}
