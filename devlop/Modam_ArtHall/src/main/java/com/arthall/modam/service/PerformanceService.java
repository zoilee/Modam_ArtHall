package com.arthall.modam.service;

import java.text.DecimalFormat;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.arthall.modam.entity.PerformanceEntity;
import com.arthall.modam.repository.CommentRepository;
import com.arthall.modam.repository.PerformanceRepository;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final CommentRepository commentRepository;

    // 생성자 주입 방식 사용, @Autowired 생략 가능
    public PerformanceService(PerformanceRepository performanceRepository, CommentRepository commentRepository) {
        this.performanceRepository = performanceRepository;
        this.commentRepository = commentRepository;
    }

    // ID로 공연 정보 가져오기
    public Optional<PerformanceEntity> getPerformanceById(Integer id) {
        return performanceRepository.findById(id);
    }

    // ID로 공연 정보 가져오고 평균 평점 추가 설정
    public Optional<PerformanceEntity> getPerformanceWithAverageRating(Integer performanceId) {
        Optional<PerformanceEntity> performanceOpt = performanceRepository.findById(performanceId);
        
        if (performanceOpt.isPresent()) {
            PerformanceEntity performance = performanceOpt.get();
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
