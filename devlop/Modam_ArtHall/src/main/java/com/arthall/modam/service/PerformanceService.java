package com.arthall.modam.service;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PerformanceService(PerformancesRepository performanceRepository,
            ReservationRepository reservationRepository) {
        this.performancesRepository = performanceRepository;
        this.reservationRepository = reservationRepository;
    }

    // 스레드-안전한 DateTimeFormatter
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public List<PerformancesEntity> getUpcomingPerformances() {
        // 현재 날짜를 가져와 SQL Date로 변환
        Date currentDate = Date.valueOf(LocalDate.now());
        // 'enddate'가 현재 날짜 이후인 공연 리스트 가져오기
        List<PerformancesEntity> performances = performancesRepository.findByEnddateAfter(currentDate);
        formatPerformanceDates(performances);

        // startdate 기준으로 정렬
        performances.sort(Comparator.comparing(PerformancesEntity::getStartdate));
        return performances;

    }

    // 지난 공연 페이징 처리
    public Page<PerformancesEntity> getPastPerformances(Pageable pageable) {
        // Repository에서 데이터 가져오기
        Page<PerformancesEntity> pastPerformances = performancesRepository.findByEnddateBefore(
                new java.sql.Date(System.currentTimeMillis()), pageable);

        // 디버깅 로그
        System.out.println("Fetched Performances: " + pastPerformances.getContent().size());

        // 날짜 포맷팅
        pastPerformances.getContent().forEach(performance -> {
            if (performance.getStartdate() != null) {
                performance.setFormattedStartDate(
                        dateFormatter.format(performance.getStartdate().toLocalDate()));
            }
            if (performance.getEnddate() != null) {
                performance.setFormattedEndDate(
                        dateFormatter.format(performance.getEnddate().toLocalDate()));
            }
        });

        return pastPerformances;
    }

    private void formatPerformanceDates(List<PerformancesEntity> performances) {
        for (PerformancesEntity performance : performances) {
            if (performance.getStartdate() != null) {
                performance.setFormattedStartDate(formatDate(performance.getStartdate()));
            }
            if (performance.getEnddate() != null) {
                performance.setFormattedEndDate(formatDate(performance.getEnddate()));
            }
        }
    }

    private String formatDate(java.sql.Date sqlDate) {
        // java.sql.Date -> java.time.LocalDate 변환
        LocalDate localDate = sqlDate.toLocalDate();
        // LocalDate를 DateTimeFormatter로 포맷팅
        return dateFormatter.format(localDate);
    }

    // 전체 목록 검색
    public List<PerformancesEntity> findAll() {
        return performancesRepository.findAll();
    }

    // title로 검색
    public List<PerformancesEntity> findByTitle(String title) {
        return performancesRepository.findByTitle(title);
    }

    // ID로 공연 정보 가져오기
    public Optional<PerformancesEntity> getPerformanceById(Integer id) {
        return performancesRepository.findById(id);
    // 모든 공연의 예약 현황 정보 가져오기 (내림차순 정렬)
    public List<PerformancesDto> getPerformancesWithReservationRate() {
        List<PerformancesEntity> performances = performancesRepository
                .findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<PerformancesDto> performanceDetails = new ArrayList<>();

        for (PerformancesEntity performance : performances) {
            int totalSeats = 100; // 예시로 전체 좌석 수를 100으로 가정
            int reservedSeats = reservationRepository.countByPerformanceId(performance.getId());
            double reservationRate = (double) reservedSeats / totalSeats * 100;

            PerformancesDto dto = PerformancesDto.toPerformancesDto(performance);
            dto.setImageUrl(performance.getImagesEntities().isEmpty() ? null
                    : performance.getImagesEntities().get(0).getImageUrl());
            dto.setReservationRate(reservationRate);
            dto.setReservationCount(reservedSeats); // 예약 수 설정

            performanceDetails.add(dto);
        }

        return performanceDetails;
    }

    // 페이지네이션을 사용하여 공연 목록 가져오기 (최신순 정렬)
    public Page<PerformancesDto> getPerformances(Pageable pageable) {
        return performancesRepository.findAll(pageable)
                .map(PerformancesDto::toPerformancesDto);
    }

    @Transactional
    public void deactivatePerformance(int performanceId) {
        // 공연 비활성화
        PerformancesEntity performance = performancesRepository.findById(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("공연을 찾을 수 없습니다."));
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

    // 최신 공연 리스트를 가져오는 메서드
    public List<PerformancesEntity> getPerformancesByDate(Date today) {
        return performancesRepository.findByStartdateBeforeAndEnddateAfter(today, today);
    }
}
