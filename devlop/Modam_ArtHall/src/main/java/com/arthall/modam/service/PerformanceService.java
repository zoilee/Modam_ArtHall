package com.arthall.modam.service;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Calendar;

import java.util.List;
import java.util.Optional;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arthall.modam.dto.PerformancesDto;
import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.entity.ShowEntity;
import com.arthall.modam.repository.CommentRepository;
import com.arthall.modam.repository.PerformancesRepository;
import com.arthall.modam.repository.ShowRepository;

@Service
public class PerformanceService {

    private final PerformancesRepository performancesRepository;
    private final CommentRepository commentRepository;
    private final ShowRepository showRepository;

    @Transactional
    public void registerPerformanceWithShows(PerformancesDto performanceDto) {
        // 1. Performance 등록
        PerformancesEntity performance = new PerformancesEntity();
        performance.setTitle(performanceDto.getTitle());
        performance.setDescription(performanceDto.getDescription());
        performance.setStartdate(performanceDto.getStartDate());
        performance.setEnddate(performanceDto.getEndDate());
        performance.setTime(performanceDto.getTime());
        performance.setAge(performanceDto.getAge());
        performance.setLocation(performanceDto.getLocation());
        
        // Performance 저장
        performancesRepository.save(performance);

        // 2. Show 등록 (startdate ~ enddate 동안 매일 13시, 15시 공연 등록)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(performanceDto.getStartDate());

        Date endDate = performanceDto.getEndDate();
        while (!calendar.getTime().after(endDate)) {
            // 매일 13시 회차와 15시 회차를 추가
            createShowForPerformance(performance, (Date) calendar.getTime(), 1); // 13시 회차
            createShowForPerformance(performance, (Date) calendar.getTime(), 2); // 15시 회차

            // 날짜를 하루씩 증가
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void createShowForPerformance(PerformancesEntity performance, Date showDate, int showTime) {
            ShowEntity show = new ShowEntity();
            show.setShowDate(showDate);
        show.setShowTime(showTime);  // 1: 13시, 2: 15시
        show.setSeatLimit(216);  // 기본값 216
        show.setSeatAvailable(216);  // 기본값 216
        show.setPerformancesEntity(performance);  // Performance 참조

        showRepository.save(show);
    }

    // 생성자 주입 방식 사용, @Autowired 생략 가능
    public PerformanceService(PerformancesRepository performancesRepository, CommentRepository commentRepository, ShowRepository showRepository) {
        this.performancesRepository = performancesRepository;
        this.commentRepository = commentRepository;
        this.showRepository = showRepository;
    }

    

    // 스레드-안전한 DateTimeFormatter
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public List<PerformancesEntity> getUpcomingPerformances(java.sql.Date currentDate) {
        List<PerformancesEntity> performances = performancesRepository.findByEnddateAfter(currentDate);
        formatPerformanceDates(performances);
        return performances;
    }

    public List<PerformancesEntity> getFinishedPerformances(java.sql.Date currentDate) {
        List<PerformancesEntity> performances = performancesRepository.findByEnddateBefore(currentDate);
        formatPerformanceDates(performances);
        return performances;
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
}
