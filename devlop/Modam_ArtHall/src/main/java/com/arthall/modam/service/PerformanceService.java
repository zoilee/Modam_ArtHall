package com.arthall.modam.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.arthall.modam.dto.PerformancesDto;
import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.repository.PerformancesRepository;
import com.arthall.modam.repository.ReservationRepository;

import jakarta.transaction.Transactional;

@Service
public class PerformanceService {

    private final PerformancesRepository performancesRepository;
    private final ReservationRepository reservationRepository;

    // 생성자 주입 방식 사용
    public PerformanceService(PerformancesRepository performanceRepository, ReservationRepository reservationRepository) {
        this.performancesRepository = performanceRepository;
        this.reservationRepository = reservationRepository;
    }

    // 모든 공연의 예약 현황 정보 가져오기
    public List<PerformancesDto> getPerformancesWithReservationRate() {
        List<PerformancesEntity> performances = performancesRepository.findAll();
        List<PerformancesDto> performanceDetails = new ArrayList<>();

        for (PerformancesEntity performance : performances) {
            int totalSeats = 100;  // 예시로 전체 좌석 수를 100으로 가정
            int reservedSeats = reservationRepository.countByPerformanceId(performance.getId());
            double reservationRate = (double) reservedSeats / totalSeats * 100;

            PerformancesDto dto = PerformancesDto.toPerformancesDto(performance);
            dto.setImageUrl(performance.getImagesEntities().isEmpty() ? null : performance.getImagesEntities().get(0).getImageUrl());
            dto.setReservationRate(reservationRate);

            performanceDetails.add(dto);
        }

        return performanceDetails;
    }

    @Transactional
    public void deactivatePerformance(int performanceId) {
        // 공연 비활성화
        PerformancesEntity performance = performancesRepository.findById(performanceId).orElseThrow(() -> new IllegalArgumentException("공연을 찾을 수 없습니다."));
        performance.setActive(false);
        performancesRepository.save(performance);
    }

    @Transactional
    public boolean deletePerformance(int performanceId) {
        int reservationCount = reservationRepository.countByPerformanceId(performanceId);
        if (reservationCount == 0) {
            // 예약이 없는 경우에만 삭제
            performancesRepository.deleteById(performanceId);
            return true;
        } else {
            return false;
        }
    }
}
