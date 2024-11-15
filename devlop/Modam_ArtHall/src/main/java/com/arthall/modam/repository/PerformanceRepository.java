package com.arthall.modam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.PerformanceEntity;

@Repository
public interface PerformanceRepository extends JpaRepository<PerformanceEntity, Integer> {
}
