package com.arthall.modam.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.dto.ShowDto;
import com.arthall.modam.repository.ShowRepository;

@Service
public class ShowService {

    @Autowired
    private ShowRepository showRepository;

    public List<ShowDto> getShowsByMusical(int musicalId) {
        return showRepository.findByMusicalId(musicalId);
    }
}

