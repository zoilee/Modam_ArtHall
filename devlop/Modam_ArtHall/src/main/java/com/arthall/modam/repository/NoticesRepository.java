package com.arthall.modam.repository;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.ImagesEntity;
import com.arthall.modam.entity.NoticesEntity;


@Repository
public interface NoticesRepository extends JpaRepository<NoticesEntity, Integer> {
    @Query("SELECT i FROM ImagesEntity i WHERE i.referenceId = :id AND i.referenceType = 'NOTICE'")
    List<ImagesEntity> findNoticesImages(@Param("id") int id);

    // 최근 공지사항 4개를 가져오는 메서드
    @Query("SELECT n FROM NoticesEntity n ORDER BY n.createdAt DESC")
    List<NoticesEntity> findTop4ByOrderByCreatedAtDesc(Pageable pageable);
}
