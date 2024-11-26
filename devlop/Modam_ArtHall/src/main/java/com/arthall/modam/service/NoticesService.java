package com.arthall.modam.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.NoticesEntity;
import com.arthall.modam.repository.NoticesRepository;

@Service
public class NoticesService {

    private final NoticesRepository noticesRepository;

    public NoticesService(NoticesRepository noticesRepository) {
        this.noticesRepository = noticesRepository;
    }

    public List<NoticesEntity> getRecentNotices(int limit) {
        return noticesRepository.findTopByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }
}
