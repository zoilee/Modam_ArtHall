package com.arthall.modam.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.RewardsLogEntity;
import com.arthall.modam.repository.RewardsLogRepository;

@Service
public class RewardsLogService {

    @Autowired
    private RewardsLogRepository rewardsLogRepository;

    // 특정 userId의 적립금 로그를 가져오는 메서드
    public Page<RewardsLogEntity> getLogsByUserId(int userId, Pageable pageable) {
        return rewardsLogRepository.findByUserId(userId, pageable);
    }

    public Optional<RewardsLogEntity> findLogByUSE(int userId, int resId) {
        return rewardsLogRepository.findByUserIdAndDescriptionAndReservationsId(userId, "USE", resId);
    }

    public RewardsLogEntity findLogByEARN(int userId, int resId) {
        return rewardsLogRepository.findByUserIdAndDescriptionAndReservationsId(userId, "EARN", resId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Can't Find by ID Log(EARN) "));
    }

}
