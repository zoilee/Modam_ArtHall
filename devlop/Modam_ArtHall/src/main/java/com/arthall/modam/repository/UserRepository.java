package com.arthall.modam.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.arthall.modam.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    
    // 로그인 ID로 회원 찾기
    Optional<UserEntity> findByLoginId(String loginId);
    
    // 이메일로 회원 찾기
    Optional<UserEntity> findByEmail(String email);
    
    Page<UserEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
