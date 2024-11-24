package com.arthall.modam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.RewardsLogEntity;

@Repository
public interface RewardsLogRepository extends JpaRepository<RewardsLogEntity, Integer> {
    Optional<RewardsLogEntity> findByUserId(int userId);


}
