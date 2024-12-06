package com.arthall.modam.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.QnaEntity;
import com.arthall.modam.repository.QnaRepository;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class QnaService{

    private final QnaRepository qnaRepository;

    // QnA 목록 조회
    public Page<QnaEntity> getAllQna(Pageable pageable) {
        return qnaRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    // 특정 QnA 조회
    public QnaEntity getQnaById(int id) {
        return qnaRepository.findById(id).orElse(null);
    }

    // QnA 생성
    public QnaEntity createQna(QnaEntity qnaEntity) {
        return qnaRepository.save(qnaEntity);
    }

    // QnA 수정
    public QnaEntity updateQna(QnaEntity qnaEntity) {
        return qnaRepository.save(qnaEntity);
    }

    // QnA 삭제
    public void deleteQna(int id) {
        qnaRepository.deleteById(id);
    }

    public Page<QnaEntity> getPagedQnaList(Pageable pageable) {
        return qnaRepository.findAll(pageable);
    }
    
    
}
