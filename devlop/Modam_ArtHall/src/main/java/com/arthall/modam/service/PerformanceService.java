package com.arthall.modam.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.PerformanceEntity;
import com.arthall.modam.repository.PerformanceRepository;

@Service
public class PerformanceService {
    
    @Autowired
    private PerformanceRepository performanceRepository;
    
    // 전체 목록 검색
    public List<PerformanceEntity> findAll() {
        return performanceRepository.findAll();
    }

    // id로 검색
    public Optional<PerformanceEntity> findById(int id) {
        return performanceRepository.findById(id);
    }

    // title로 검색
    public List<PerformanceEntity> findByTitle(String title) {
        return performanceRepository.findByTitleContaining(title);
    }
}
