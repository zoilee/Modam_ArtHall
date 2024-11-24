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
    public void deductPoints(int userId, BigDecimal points,int reservationId) {
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
        rewardLogsEntity.setDescription("USE");
        rewardLogsEntity.setReservationsId(reservationId);
        rewardLogsEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
 
        rewardLogsRepository.save(rewardLogsEntity);


        log.info("사용자 ID {}의 적립금 {}원 차감 완료", userId, points);
    }

    @Transactional
    public void addPoints(int userId, BigDecimal points, String description, int reservationId  ) {
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
        addPoints(userId, points, "환불로 인한 적립금 반환", reservationId);
    }
}
