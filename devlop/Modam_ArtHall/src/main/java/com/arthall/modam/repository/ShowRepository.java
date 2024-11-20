package com.arthall.modam.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthall.modam.entity.ShowEntity;

public interface ShowRepository extends JpaRepository<ShowEntity, Integer> {
    List<ShowEntity> findByPerformanceId(int performanceId);

    List<ShowEntity> findByShowDate(Date showDate);

    List<ShowEntity> findByPerformanceIdAndShowDateAndShowTime(int performanceId, LocalDate showDate, int showTime);
}
