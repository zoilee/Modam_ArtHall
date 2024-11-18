package com.arthall.modam.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.CommentEntity;
import com.arthall.modam.entity.PerformancesEntity;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    // performanceId에 따라 댓글을 페이지로 가져오는 메서드
    Page<CommentEntity> findByPerformanceOrderByCreatedAtDesc(PerformancesEntity performance, Pageable pageable);

    @Query("SELECT c FROM CommentEntity c LEFT JOIN FETCH c.user LEFT JOIN FETCH c.performance WHERE c.performance.id = :performanceId")
    List<CommentEntity> findCommentsByPerformanceId(@Param("performanceId") Integer performanceId);

    @Query("SELECT AVG(c.rating) FROM CommentEntity c WHERE c.performance.id = :performanceId")
    Double findAverageRatingByPerformanceId(@Param("performanceId") Integer performanceId);

    // Pageable을 사용하는 쿼리
    @Query("SELECT c FROM CommentEntity c WHERE c.performance.id = :performanceId ORDER BY c.createdAt DESC")
    List<CommentEntity> findCommentsByPerformanceId(@Param("performanceId") int performanceId, Pageable pageable);

    // Pageable 없이 전체 데이터 조회
    @Query("SELECT c FROM CommentEntity c WHERE c.performance.id = :performanceId ORDER BY c.createdAt DESC")
    List<CommentEntity> findCommentsByPerformanceId(@Param("performanceId") int performanceId);

}