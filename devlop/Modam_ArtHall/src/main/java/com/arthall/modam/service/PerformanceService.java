package com.arthall.modam.service;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.arthall.modam.dto.PerformancesDto;
import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.entity.ShowEntity;
import com.arthall.modam.repository.CommentRepository;
import com.arthall.modam.repository.PerformancesRepository;
import com.arthall.modam.repository.ReservationsRepository;

import jakarta.transaction.Transactional;
import com.arthall.modam.repository.ShowRepository;

@Service
public class PerformanceService {
    private final PerformancesRepository performancesRepository;
    private final CommentRepository commentRepository;
    @Autowired
    private ReservationsRepository reservationsRepository;
    private final ShowRepository showRepository;

    public void registerShowsWithPerformance(PerformancesEntity performance) {
        // Show 등록 (startdate ~ enddate 동안 매일 13시, 17시 공연 등록)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date(performance.getStartDate().getTime())); // java.sql.Date -> java.util.Date

        Date endDate = performance.getEndDate(); // performance.getEnddate()는 java.sql.Date

        while (!calendar.getTime().after(endDate)) {
            // 매일 13시 회차와 17시 회차를 추가
            createShowForPerformance(performance, new java.sql.Date(calendar.getTimeInMillis()), 1); // 13시 회차
            createShowForPerformance(performance, new java.sql.Date(calendar.getTimeInMillis()), 2); // 17시 회차

            // 날짜를 하루씩 증가
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void createShowForPerformance(PerformancesEntity performance, Date showDate, int showTime) {
        ShowEntity show = new ShowEntity();
        show.setShowDate(showDate);
        show.setShowTime(showTime); // 1: 13시, 2: 15시
        show.setSeatLimit(216); // 기본값 216
        show.setSeatAvailable(216); // 기본값 216
        show.setPerformancesEntity(performance); // Performance 참조

        showRepository.save(show);
    }

    // 생성자 주입 방식 사용, @Autowired 생략 가능
    public PerformanceService(PerformancesRepository performancesRepository, CommentRepository commentRepository,
            ShowRepository showRepository) {
        this.performancesRepository = performancesRepository;
        this.commentRepository = commentRepository;
        this.showRepository = showRepository;
    }

    // 스레드-안전한 DateTimeFormatter
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public List<PerformancesEntity> getUpcomingPerformances() {
        // 현재 날짜를 가져와 SQL Date로 변환
        Date currentDate = Date.valueOf(LocalDate.now());
        // 'enddate'가 현재 날짜 이후인 공연 리스트 가져오기
        List<PerformancesEntity> performances = performancesRepository.findByEndDateAfter(currentDate);
        formatPerformanceDates(performances);

        // startdate 기준으로 정렬
        performances.sort(Comparator.comparing(PerformancesEntity::getStartDate));
        return performances;

    }

    // 지난 공연 페이징 처리
    public Page<Map<String, Object>> getPastPerformances(Pageable pageable) {
        Page<PerformancesEntity> pastPerformances = performancesRepository.findPastPerformances(pageable);
    
        // 필요한 필드만 추출하여 Page<Map> 형태로 변환
        return pastPerformances.map(performance -> {
            Map<String, Object> performanceMap = new HashMap<>();
            performanceMap.put("id", performance.getId());
            performanceMap.put("title", performance.getTitle());
            performanceMap.put("startDate", performance.getStartDate() != null
                    ? dateFormatter.format(performance.getStartDate().toLocalDate())
                    : "데이터 없음");
            performanceMap.put("endDate", performance.getEndDate() != null
                    ? dateFormatter.format(performance.getEndDate().toLocalDate())
                    : "데이터 없음");
            return performanceMap;
        });
    }

    private void formatPerformanceDates(List<PerformancesEntity> performances) {
        for (PerformancesEntity performance : performances) {
            if (performance.getStartDate() != null) {
                performance.setFormattedStartDate(formatDate(performance.getStartDate()));
            }
            if (performance.getEndDate() != null) {
                performance.setFormattedEndDate(formatDate(performance.getEndDate()));
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
    }

    // ID로 공연 정보 가져오고 평균 평점 추가 설정
    public Optional<PerformancesEntity> getPerformanceWithAverageRating(Integer performanceId) {
        Optional<PerformancesEntity> performanceOpt = performancesRepository.findById(performanceId);

        if (performanceOpt.isPresent()) {
            PerformancesEntity performance = performanceOpt.get();
            Double averageRating = commentRepository.findAverageRatingByPerformanceId(performanceId);

            // 소수점 한 자리까지 포맷하여 평균 평점 설정
            DecimalFormat df = new DecimalFormat("#.#");
            String formattedRating = (averageRating != null) ? df.format(averageRating) : "0.0";
            performance.setFormattedAverageRating(formattedRating);

            return Optional.of(performance);
        } else {
            return Optional.empty(); // 공연이 없을 경우 빈 Optional 반환
        }
    }

    // 현재 상연 중인 공연 가져오기
    public List<PerformancesEntity> getCurrentPerformances(Date today) {
        return performancesRepository.findByStartDateBeforeAndEndDateAfter(today, today);
    }

    // 모든 공연의 예약 현황 정보 가져오기 (내림차순 정렬)
    public List<PerformancesDto> getPerformancesWithReservationRate() {
        List<PerformancesEntity> performances = performancesRepository
                .findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<PerformancesDto> performanceDetails = new ArrayList<>();

        for (PerformancesEntity performance : performances) {
            int totalSeats = 100; // 예시로 전체 좌석 수를 100으로 가정
            int reservedSeats = reservationsRepository.countByPerformanceId(performance.getId());
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
        int reservationCount = reservationsRepository.countByPerformanceId(performanceId);
        if (reservationCount == 0) {
            // 예약이 없는 경우에만 삭제
            performancesRepository.deleteById(performanceId);
            return true;
        } else {
            return false;
        }
    }

    public String findTitleById(int id) {
        PerformancesEntity performance = performancesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공연을 찾을 수 없습니당"));
        String title = performance.getTitle();
        return title;
    }
}
