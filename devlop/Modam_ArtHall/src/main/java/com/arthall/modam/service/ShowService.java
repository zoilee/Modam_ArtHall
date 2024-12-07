package com.arthall.modam.service;

import java.sql.Date;

import java.util.List;

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
}
