package com.arthall.modam.service;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import java.time.format.DateTimeFormatter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.repository.CommentRepository;
import com.arthall.modam.repository.PerformancesRepository;

@Service
public class PerformanceService {

    private final PerformancesRepository performancesRepository;
    private final CommentRepository commentRepository;

    // 생성자 주입 방식 사용, @Autowired 생략 가능
    public PerformanceService(PerformancesRepository performanceRepository, CommentRepository commentRepository) {
        this.performancesRepository = performanceRepository;
        this.commentRepository = commentRepository;
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
                new java.sql.Date(System.currentTimeMillis()), pageable
            );
        
            // 디버깅 로그
            System.out.println("Fetched Performances: " + pastPerformances.getContent().size());
        
            // 날짜 포맷팅
            pastPerformances.getContent().forEach(performance -> {
                if (performance.getStartdate() != null) {
                    performance.setFormattedStartDate(
                        dateFormatter.format(performance.getStartdate().toLocalDate())
                    );
                }
                if (performance.getEnddate() != null) {
                    performance.setFormattedEndDate(
                        dateFormatter.format(performance.getEnddate().toLocalDate())
                    );
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
