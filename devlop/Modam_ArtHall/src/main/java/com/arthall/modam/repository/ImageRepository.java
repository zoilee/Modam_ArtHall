package com.arthall.modam.repository;

import com.arthall.modam.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    List<Image> findByReferenceIdAndReferenceType(Integer referenceId, String referenceType);
}
