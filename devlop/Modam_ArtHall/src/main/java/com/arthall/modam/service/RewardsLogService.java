package com.arthall.modam.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.RewardsLogEntity;
import com.arthall.modam.repository.RewardsLogRepository;

@Service
public class RewardsLogService {

    @Autowired
    private RewardsLogRepository rewardsLogRepository;

    public List<RewardsLogEntity> getRewardsLogByUserId(int userId) {
        // 사용자 ID로 적립금 로그 조회
        return rewardsLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
