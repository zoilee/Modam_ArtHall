package com.arthall.modam.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.ImagesEntity;
import com.arthall.modam.repository.ImagesRepository;

@Service
public class ImagesService {
    @Autowired
    ImagesRepository imagesRepository;

    public List<ImagesEntity> findAllById(List<Integer> ids) {
        return imagesRepository.findAllById(ids);
    }

    public void deleteImg(ImagesEntity image) {
        imagesRepository.delete(image);
    }

    public void saveImg(ImagesEntity image) {
        imagesRepository.save(image);
    }

    public List<ImagesEntity> findByReferenceIdAndReferenceType(int id, ImagesEntity.ReferenceType referenceType) {
        return imagesRepository.findByReferenceIdAndReferenceType(id, referenceType);
    }

    public void deleteAllImg(List<ImagesEntity> images) {
        imagesRepository.deleteAll(images);
    }

}
