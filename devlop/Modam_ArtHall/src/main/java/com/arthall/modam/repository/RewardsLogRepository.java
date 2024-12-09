package com.arthall.modam.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.RewardsLogEntity;

@Repository
public interface RewardsLogRepository extends JpaRepository<RewardsLogEntity, Integer> {
    // 단일 사용자 ID로 가장 최근의 적립금 로그 조회
    Optional<RewardsLogEntity> findFirstByUserIdOrderByCreatedAtDesc(int userId);

    // 사용자 ID로 적립금 로그 전체 조회
    Page<RewardsLogEntity> findByUserId(int userId, Pageable pageable);
    

    Optional<RewardsLogEntity> findByReservationsId(int id);

    Optional<RewardsLogEntity> findByUserIdAndDescriptionAndReservationsId(int userId, String description, int resId);
}
