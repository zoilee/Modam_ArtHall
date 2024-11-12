package com.arthall.modam.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.dto.MusicalDto;
import com.arthall.modam.repository.MusicalRepository;

@Service
public class MusicalService {

    @Autowired
    private MusicalRepository musicalRepository;

    public List<MusicalDto> getAllMusicals() {
        return musicalRepository.findAll();
    }

    public MusicalDto getMusical(int musicalId) {
        return musicalRepository.findById(musicalId).orElseThrow(() -> new RuntimeException("해당 공연을 찾을 수 없습니다."));
    }
}

