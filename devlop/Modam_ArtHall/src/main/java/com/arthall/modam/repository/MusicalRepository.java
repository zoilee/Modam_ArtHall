package com.arthall.modam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.dto.PerformanceDto;

@Repository
public interface MusicalRepository extends JpaRepository<PerformanceDto, Integer> {
}