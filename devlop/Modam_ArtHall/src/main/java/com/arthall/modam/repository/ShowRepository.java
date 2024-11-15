package com.arthall.modam.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.PerformanceEntity;
import com.arthall.modam.entity.ShowEntity;

@Repository
public interface ShowRepository extends JpaRepository<ShowEntity, Integer> {
    ShowEntity findByMusicalAndShowDateAndShowTime(PerformanceEntity musical, LocalDate showDate, int showTime);

    List<ShowEntity> findByPerformanceId(int performanceId);
}

