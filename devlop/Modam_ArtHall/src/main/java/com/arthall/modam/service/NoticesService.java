package com.arthall.modam.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.NoticesEntity;
import com.arthall.modam.repository.NoticesRepository;

@Service
public class NoticesService {

    private final NoticesRepository noticesRepository;

    public NoticesService(NoticesRepository noticesRepository) {
        this.noticesRepository = noticesRepository;
    }

    // 최근 공지사항 4개를 가져오는 메서드
    public List<NoticesEntity> getRecentNotices(int count) {
        // Pageable을 사용해 제한된 공지사항을 가져옴
        Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        return noticesRepository.findTop4ByOrderByCreatedAtDesc(pageable);
    }
}
