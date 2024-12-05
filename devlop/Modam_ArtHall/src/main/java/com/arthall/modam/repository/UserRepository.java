package com.arthall.modam.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.arthall.modam.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    
    // 로그인 ID로 회원 찾기
    Optional<UserEntity> findByLoginId(String loginId);
    
    // 이메일로 회원 찾기
    Optional<UserEntity> findByEmail(String email);
    // 이름으로 사용자 찾기 (필요한 경우 유지)
    Optional<UserEntity> findByName(String name);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByNameAndEmail(String name, String email); // 이름과 이메일로 사용자 검색
    Optional<UserEntity> findByLoginIdAndEmail(String loginId, String email); //아이디와 이메일로 비밀번호 찾기
    Optional<UserEntity> findByLoginIdAndStatus(String loginId, UserEntity.Status status); // 아이디와 사용자 상태
    
    Page<UserEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // 총 가입자 수 계산
    @Query("SELECT COUNT(u) FROM UserEntity u")
    long countTotalUsers();
    // 최근 가입자 목록 가져오기
    @Query("SELECT u FROM UserEntity u WHERE u.createdAt >= :oneWeekAgo ORDER BY u.createdAt DESC")
    Page<UserEntity> findUsersRegisteredInLastWeek(@Param("oneWeekAgo") LocalDateTime oneWeekAgo, Pageable pageable);
}
