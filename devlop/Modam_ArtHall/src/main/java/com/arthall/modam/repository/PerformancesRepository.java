package com.arthall.modam.repository;

import com.arthall.modam.entity.ImagesEntity;
import com.arthall.modam.entity.PerformancesEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PerformancesRepository extends JpaRepository<PerformancesEntity, Integer> {
    @Query("SELECT i FROM ImagesEntity i WHERE i.referenceId = :id AND i.referenceType = 'PERFORMANCE'")
    List<ImagesEntity> findPerformanceImages(@Param("id") int id);

    List<PerformancesEntity> findByTitle(String title);

    Optional<PerformancesEntity> getPerformancesById(int id);

    Page<PerformancesEntity> findByEnddateBefore(Date date, Pageable pageable);

    List<PerformancesEntity> findByEnddateAfter(Date date);

    // 지난 공연 가져오기
    @Query("SELECT p FROM PerformancesEntity p WHERE p.enddate < CURRENT_DATE")
    Page<PerformancesEntity> findPastPerformances(Pageable pageable);

    // 현재 및 미래 공연 가져오기
    @Query("SELECT p FROM PerformancesEntity p WHERE p.enddate >= CURRENT_DATE ORDER BY p.startdate ASC")
    List<PerformancesEntity> findUpcomingPerformances();

    // 최신 공연 데이터를 가져오는 메서드
    List<PerformancesEntity> findByStartdateBeforeAndEnddateAfter(Date startdate, Date enddate);

}
