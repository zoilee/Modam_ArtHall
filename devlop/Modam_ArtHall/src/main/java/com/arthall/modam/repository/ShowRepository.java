package com.arthall.modam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthall.modam.entity.ShowEntity;

import java.time.LocalDate;
import java.util.List;

public interface ShowRepository extends JpaRepository<ShowEntity, Integer> {
    List<ShowEntity> findByPerformanceId(int performanceId);

    List<ShowEntity> findByShowDate(LocalDate showDate);

    List<ShowEntity> findByPerformanceIdAndShowDateAndShowTime(int performanceId, LocalDate showDate, int showTime);
}

