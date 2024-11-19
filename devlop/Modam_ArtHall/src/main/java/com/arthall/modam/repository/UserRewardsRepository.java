package com.arthall.modam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.UserRewardEntity;

@Repository
public interface UserRewardsRepository extends JpaRepository<UserRewardEntity, Integer> {
    Optional<UserRewardEntity> findByUserId(int userId);
}
