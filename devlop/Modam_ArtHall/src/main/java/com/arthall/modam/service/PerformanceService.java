package com.arthall.modam.service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

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
