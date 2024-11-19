package com.arthall.modam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthall.modam.entity.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, Integer>{
    Optional<UserEntity> findByPlatformId(String platformId);
    Optional<UserEntity> findByLoginId(String loginId);
    Optional<UserEntity> findByEmail(String email);
}
