package com.arthall.modam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthall.modam.entity.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, Integer>{
    Optional<UserEntity> findByLoginId(String loginId);
    Optional<UserEntity> findByEmail(String email);
    // 이름으로 사용자 찾기 (필요한 경우 유지)
    Optional<UserEntity> findByName(String name);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByNameAndEmail(String name, String email); // 이름과 이메일로 사용자 검색
    Optional<UserEntity> findByLoginIdAndEmail(String loginId, String email); //아이디와 이메일로 비밀번호 찾기
    Optional<UserEntity> findByLoginIdAndStatus(String loginId, String status); // 아이디와 사용자 상태
}
