package com.arthall.modam.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.dto.PerformanceDto;
import com.arthall.modam.entity.PerformanceEntity;
import com.arthall.modam.repository.MusicalRepository;

@Service
public class MusicalService {

    @Autowired
    private MusicalRepository musicalRepository;

    public List<PerformanceDto> getAllPerformance() {
        List<PerformanceEntity> performances = musicalRepository.findAll();
        return performances.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public PerformanceDto getPerformance(int performanceId) {
        PerformanceEntity performance = musicalRepository.findById(performanceId)
            .orElseThrow(() -> new RuntimeException("해당 공연을 찾을 수 없습니다."));
        return convertToDto(performance);
    }

    public PerformanceDto getPerformanceByTitle(String title) {
        PerformanceEntity performance = musicalRepository.findByTitle(title);
        if (performance == null) {
            throw new RuntimeException("해당 공연을 찾을 수 없습니다.");
        }
        return convertToDto(performance);
    }

    private PerformanceDto convertToDto(PerformanceEntity performance) {
        PerformanceDto dto = new PerformanceDto();
        dto.setId(performance.getId());
        dto.setTitle(performance.getTitle());
        dto.setDescription(performance.getDescription());
        dto.setStartDate(performance.getStartDate());
        dto.setEndDate(performance.getEndDate());
        dto.setTime(performance.getTime());
        dto.setAge(performance.getAge());
        dto.setLocation(performance.getLocation());
        dto.setCreated_at(performance.getCreatedAt());
        return dto;
    }
}


