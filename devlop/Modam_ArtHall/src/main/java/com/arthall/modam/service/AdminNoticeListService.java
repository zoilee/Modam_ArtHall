package com.arthall.modam.service;

import com.arthall.modam.entity.NoticesEntity;
import com.arthall.modam.repository.NoticesRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminNoticeListService {

    @Autowired
    private NoticesRepository adminNoticeListRepository;

    // 공지사항 목록 조회 (페이징 적용)
    public Page<NoticesEntity> getNotices(int page, int size) {
        int validatedSize = (size > 0) ? size : 5; // 기본값을 5로 지정
        Pageable pageable = PageRequest.of(page, validatedSize, Sort.by(Sort.Direction.DESC, "id"));
        return adminNoticeListRepository.findAll(pageable);
    }

    // 공지사항 상세 조회 (수정 시 필요)
    public Optional<NoticesEntity> getNoticeById(int id) {
        return adminNoticeListRepository.findById(id);
    }

    // 공지사항 저장 (수정 포함)
    @Transactional
    public NoticesEntity saveNotice(NoticesEntity notice) {
        return adminNoticeListRepository.save(notice);
    }

    // 공지사항 삭제
    public void deleteNotice(int id) {
        adminNoticeListRepository.deleteById(id);
    }
}
