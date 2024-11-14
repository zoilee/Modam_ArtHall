package com.arthall.modam.repository;

import com.arthall.modam.entity.ImagesEntity;
import com.arthall.modam.entity.PerformancesEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PerformancesRepository extends JpaRepository<PerformancesEntity, Integer> {
    @Query("SELECT i FROM ImagesEntity i WHERE i.referenceId = :performanceId AND i.referenceType = 'PERFORMANCE'")
    List<ImagesEntity> findPerformanceImages(@Param("performanceId") int performanceId);
}
