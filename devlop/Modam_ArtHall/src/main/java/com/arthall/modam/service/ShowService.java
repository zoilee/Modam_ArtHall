package com.arthall.modam.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.PerformanceEntity;
import com.arthall.modam.entity.ShowEntity;
import com.arthall.modam.repository.ShowRepository;

@Service
public class ShowService {

    @Autowired
    PerformanceEntity performanceEntity = new PerformanceEntity();

    // ShowRepository를 주입받음
    private final ShowRepository showRepository;

    public ShowService(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    // 전체 ShowEntity 목록을 반환하는 메서드
    public List<ShowEntity> getAllShows() {
        return showRepository.findAll();
    }

    // performanceId로 검색한 목록을 반환하는 메서드
    public List<ShowEntity> getShowsByPerformanceId() {
        var performanceId = performanceEntity.getId();
        return showRepository.findByPerformanceId(performanceId);
    }

    // 특정 ShowEntity를 showId로 조회하는 메서드
    public Optional<ShowEntity> getShowById(int showId) {
        return showRepository.findById(showId);
    }

    // 새로운 ShowEntity를 저장하는 메서드
    public ShowEntity createShow(ShowEntity showEntity) {
        return showRepository.save(showEntity);
    }

    // 기존 ShowEntity를 수정하는 메서드
    public ShowEntity updateShow(int showId, ShowEntity showEntity) {
        if (showRepository.existsById(showId)) {
            showEntity.setShowId(showId);  // ID는 업데이트되지 않지만, 주어진 ID를 세팅
            return showRepository.save(showEntity);
        }
        return null; // 존재하지 않으면 null 반환 (예외 처리를 추가할 수도 있음)
    }

    // 특정 ShowEntity를 삭제하는 메서드
    public boolean deleteShow(int showId) {
        if (showRepository.existsById(showId)) {
            showRepository.deleteById(showId);
            return true;
        }
        return false; // 존재하지 않으면 삭제하지 않음
    }
}

