package com.arthall.modam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.RewardsLogEntity;
import java.math.BigDecimal;

@Repository
public interface RewardsLogRepository extends JpaRepository<RewardsLogEntity, Integer> {
    Optional<RewardsLogEntity> findByUserId(int userId);

    // 사용자 ID로 적립금 로그 조회 (최근 내역 순서)
    List<RewardsLogEntity> findByUserIdOrderByCreatedAtDesc(int userId);

    Optional<RewardsLogEntity> findByReservationsId(int id);

    Optional<RewardsLogEntity> findByUserIdAndDescriptionAndReservationsId(int userId, String description, int resId);
}
