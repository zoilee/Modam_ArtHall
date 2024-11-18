package com.arthall.modam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.PerformanceEntity;

@Repository
public interface MusicalRepository extends JpaRepository<PerformanceEntity, Integer> {

    // 공연 제목으로 공연을 찾는 메서드 (title 필드로 검색)
    PerformanceEntity findByTitle(String title);
}
