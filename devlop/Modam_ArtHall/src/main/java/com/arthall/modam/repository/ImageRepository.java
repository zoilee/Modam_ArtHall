package com.arthall.modam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.ImageEntity;



@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Integer> {
    // 특정 referenceId와 referenceType에 맞는 이미지 URL 가져오기
    List<ImageEntity> findByReferenceTypeAndReferenceId(ImageEntity.ReferenceType referenceType, int referenceId);
}
