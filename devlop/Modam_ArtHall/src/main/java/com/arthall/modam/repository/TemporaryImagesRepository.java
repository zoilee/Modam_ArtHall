package com.arthall.modam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.arthall.modam.entity.TemporaryImagesEntity;

public interface TemporaryImagesRepository extends JpaRepository<TemporaryImagesEntity, Integer> {
    List<TemporaryImagesEntity> findByReferenceTokenIn(List<String> referenceToken);

}
