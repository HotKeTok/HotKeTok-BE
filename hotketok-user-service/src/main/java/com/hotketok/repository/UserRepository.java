package com.hotketok.repository;

import com.hotketok.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIdIn(List<Long> userIds); // 여러 개의 ID로 사용자 목록 조회

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogInId(String logInId);
}
