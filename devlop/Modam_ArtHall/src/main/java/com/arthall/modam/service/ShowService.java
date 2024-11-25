package com.arthall.modam.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.ShowEntity;
import com.arthall.modam.repository.ShowRepository;

@Service
public class ShowService {
    
    @Autowired
    private ShowRepository showRepository;
    
    // 전체 목록 검색
    public List<ShowEntity> findAll() {
        return showRepository.findAll();
    }

    // performanceId로 검색
    public List<ShowEntity> findByPerformanceId(int performanceId) {
        return showRepository.findByPerformancesEntity_Id(performanceId);
    }

    // showDate로 검색
    public List<ShowEntity> findByShowDate(Date showDate) {
        return showRepository.findByShowDate(showDate);
    }

    // performanceId, showDate, showTime으로 검색
    public List<ShowEntity> findByPerformanceIdAndShowDateAndShowTime(int performanceId, Date showDate, int showTime) {
        return showRepository.findByPerformancesEntity_IdAndShowDateAndShowTime(performanceId, showDate, showTime);
    }

    //예약 시 예약가능좌석 차감
    public void updateSeatAvailability(int showId, int numberOfPeople) {
        // 해당 showId에 해당하는 show_entity를 DB에서 가져옴
        Optional<ShowEntity> showEntityOptional = showRepository.findById(showId);
        
        if (showEntityOptional.isPresent()) {
            ShowEntity showEntity = showEntityOptional.get();
    
            int currentAvailableSeats = showEntity.getSeatAvailable();
    
            // 좌석이 부족한 경우 예외 처리
            if (currentAvailableSeats < numberOfPeople) {
                throw new IllegalArgumentException("예약할 수 있는 좌석이 부족합니다.");
            }
    
            // 좌석 수 차감
            showEntity.setSeatAvailable(currentAvailableSeats - numberOfPeople);
    
            // 변경된 값 저장
            showRepository.save(showEntity);
        } else {
            throw new IllegalArgumentException("해당 회차를 찾을 수 없습니다.");
        }
    }
}
