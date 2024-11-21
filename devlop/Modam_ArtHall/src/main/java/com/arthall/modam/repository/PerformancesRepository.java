package com.arthall.modam.repository;

import com.arthall.modam.entity.ImagesEntity;
import com.arthall.modam.entity.PerformancesEntity;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PerformancesRepository extends JpaRepository<PerformancesEntity, Integer> {
    @Query("SELECT i FROM ImagesEntity i WHERE i.referenceId = :id AND i.referenceType = 'PERFORMANCE'")
    List<ImagesEntity> findPerformanceImages(@Param("id") int id);

    List<PerformancesEntity> findByTitleContaining(String title);

    // 현재 공연 (endDate가 오늘 이후)
    List<PerformancesEntity> findByEnddateAfter(Date currentDate);

    // 지난 공연 (endDate가 오늘 이전)
    List<PerformancesEntity> findByEnddateBefore(Date currentDate);
}
