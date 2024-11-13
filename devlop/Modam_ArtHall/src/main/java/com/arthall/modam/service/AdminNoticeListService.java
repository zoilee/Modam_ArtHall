package com.arthall.modam.service;

import com.arthall.modam.entity.AdminNoticeList;
import com.arthall.modam.repository.AdminNoticeListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminNoticeListService {

    @Autowired
    private AdminNoticeListRepository adminNoticeListRepository;

    // 공지사항 목록 조회 (페이징 적용)
    public Page<AdminNoticeList> getNotices(int page, int size) {
        int validatedSize = (size > 0) ? size : 5; // 기본값을 5로 지정
        Pageable pageable = PageRequest.of(page, validatedSize);
        return adminNoticeListRepository.findAll(pageable);
    }
    
    
    // 공지사항 상세 조회 (수정 시 필요)
    public Optional<AdminNoticeList> getNoticeById(Long id) {
        return adminNoticeListRepository.findById(id);
    }

    // 공지사항 저장 (수정 포함)
    public AdminNoticeList saveNotice(AdminNoticeList notice) {
        return adminNoticeListRepository.save(notice);
    }

    // 공지사항 삭제
    public void deleteNotice(Long id) {
        adminNoticeListRepository.deleteById(id);
    }
}
