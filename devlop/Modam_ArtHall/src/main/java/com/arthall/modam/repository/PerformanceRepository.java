package com.arthall.modam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthall.modam.entity.PerformanceEntity;

import java.util.List;

public interface PerformanceRepository extends JpaRepository<PerformanceEntity, Integer> {
    List<PerformanceEntity> findByTitleContaining(String title);
}

