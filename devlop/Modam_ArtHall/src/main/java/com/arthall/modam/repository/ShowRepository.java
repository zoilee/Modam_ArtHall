package com.arthall.modam.repository;

import java.sql.Date;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthall.modam.entity.ShowEntity;

public interface ShowRepository extends JpaRepository<ShowEntity, Integer> {
    List<ShowEntity> findByPerformancesEntity_Id(int performanceId);

    List<ShowEntity> findByShowDate(Date showDate);

    List<ShowEntity> findByPerformancesEntity_IdAndShowDateAndShowTime(int performanceId, java.util.Date showDate, int showTime);
}
