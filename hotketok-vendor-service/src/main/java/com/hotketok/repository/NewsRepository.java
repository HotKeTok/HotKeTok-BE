package com.hotketok.repository;

import com.hotketok.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findByVendorId(Long vendorId);
}
