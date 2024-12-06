package com.arthall.modam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.QnaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface QnaRepository extends JpaRepository <QnaEntity,Integer>{
    List<QnaEntity> findAllByOrderByCreatedAtDesc();
    
    Page<QnaEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
} 