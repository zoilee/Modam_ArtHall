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

    // userId와 reservationsId를 기반으로 적립금 로그 가져오기
    public List<RewardsLogEntity> getLogsByUserIdAndReservationId(int userId, int reservationsId) {
        return rewardsLogRepository.findByUserIdAndReservationsId(userId, reservationsId);
    }
}
