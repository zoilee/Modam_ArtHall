package com.arthall.modam.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.ImageEntity;
import com.arthall.modam.repository.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    // referenceId와 referenceType에 따라 첫 번째 이미지 URL을 가져오는 메서드
    public String getImageUrlByReferenceIdAndType(int referenceId, ImageEntity.ReferenceType referenceType) {
        List<ImageEntity> images = imageRepository.findByReferenceTypeAndReferenceId(referenceType, referenceId);
        return images.isEmpty() ? "/default/image/path.png" : images.get(0).getImageUrl();
    }
}
