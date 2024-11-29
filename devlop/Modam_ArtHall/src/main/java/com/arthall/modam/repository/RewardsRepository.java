package com.arthall.modam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.RewardsEntity;

@Repository
public interface RewardsRepository extends JpaRepository<RewardsEntity, Integer> {
    Optional<RewardsEntity> findByUserId(int userId);


}
