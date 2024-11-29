package com.arthall.modam.service;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arthall.modam.entity.RewardsEntity;
import com.arthall.modam.entity.RewardsLogEntity;
import com.arthall.modam.repository.RewardsLogRepository;
import com.arthall.modam.repository.RewardsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RewardsService {
    @Autowired
    RewardsRepository rewardsRepository;
    @Autowired
    RewardsLogRepository rewardLogsRepository;

    @Transactional
    public void deductPoints(int userId, BigDecimal points, int reservationId, String status) {
        RewardsEntity rewards = rewardsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자의 적립금을 찾을 수 없습니다."));

        BigDecimal newTotal = rewards.getTotalPoint().subtract(points);
        if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("적립금이 부족합니다.");
        }
        // 적립금 업데이트
        rewards.setTotalPoint(newTotal);
        rewardsRepository.save(rewards);

        // 로그 기록
        RewardsLogEntity rewardLogsEntity = new RewardsLogEntity();
        rewardLogsEntity.setUserId(userId);
        rewardLogsEntity.setChangePoint(points.negate()); // 음수로 기록
        rewardLogsEntity.setTotalPoint(newTotal);
        rewardLogsEntity.setDescription(status);
        rewardLogsEntity.setReservationsId(reservationId);
        rewardLogsEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        rewardLogsRepository.save(rewardLogsEntity);

        log.info("사용자 ID {}의 적립금 {}원 차감 완료", userId, points);
    }

    @Transactional
    public void addPoints(int userId, BigDecimal points, String description, int reservationId) {
        RewardsEntity rewards = rewardsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // 새 적립금 객체 생성
                    RewardsEntity newReward = new RewardsEntity();
                    newReward.setUserId(userId);
                    newReward.setTotalPoint(BigDecimal.ZERO); // 초기 적립금 0
                    return newReward;
                });

        BigDecimal newTotal = rewards.getTotalPoint().add(points);
        rewards.setTotalPoint(newTotal);
        rewardsRepository.save(rewards);

        // 로그 기록
        RewardsLogEntity rewardLogsEntity = new RewardsLogEntity();
        rewardLogsEntity.setUserId(userId);
        rewardLogsEntity.setChangePoint(points);
        rewardLogsEntity.setReservationsId(reservationId);
        rewardLogsEntity.setTotalPoint(newTotal);
        rewardLogsEntity.setDescription(description);
        rewardLogsEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        rewardLogsRepository.save(rewardLogsEntity);
        log.info("사용자 ID {}의 적립금 {}원 추가: {}", userId, points, description);
    }

    @Transactional
    public void rollbackPoints(int userId, BigDecimal points, int reservationId) {

        // 적립금 반환
        RewardsEntity userRewards = getRewardsByUserId(userId); // userid 로 reward 가져오기
        BigDecimal currentPoints = userRewards.getTotalPoint(); // reward의 토탈 포인트 가져오기
        BigDecimal updatedPoints = currentPoints.add(points); // 적립금 반환
        userRewards.setTotalPoint(updatedPoints); // reward 에 적립금저장
        rewardsRepository.save(userRewards); // DB에 저장

        // reward_log 저장
        RewardsLogEntity rewardLogsEntity = new RewardsLogEntity();
        rewardLogsEntity.setUserId(userId);
        rewardLogsEntity.setChangePoint(points); // 반환된 적립금 양수로 기록
        rewardLogsEntity.setReservationsId(reservationId); // 해당 예약 ID
        rewardLogsEntity.setTotalPoint(updatedPoints); // 업데이트된 적립금
        rewardLogsEntity.setDescription("REFUND");
        rewardLogsEntity.setCreatedAt(new Timestamp(System.currentTimeMillis())); // 생성 시간 설정
        rewardLogsRepository.save(rewardLogsEntity); // 로그 저장

    }

    public RewardsEntity getRewardsByUserId(int userId) {
        // 적립금 정보를 조회하고 없을 경우 기본값 반환
        return rewardsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    RewardsEntity defaultRewards = new RewardsEntity();
                    defaultRewards.setUserId(userId);
                    defaultRewards.setTotalPoint(BigDecimal.ZERO); // 기본값 0 포인트
                    return defaultRewards;
                });
    }
}
