package com.arthall.modam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthall.modam.entity.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, Integer>{
    UserEntity findByLoginId(String loginId);
}
