package com.arthall.modam.repository;

import com.arthall.modam.entity.ImagesEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagesRepository extends JpaRepository<ImagesEntity, Integer> {
    List<ImagesEntity> findByReferenceIdAndReferenceType(int referenceId, ImagesEntity.ReferenceType referenceType);

}
