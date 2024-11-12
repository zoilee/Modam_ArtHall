package com.arthall.modam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arthall.modam.dto.MusicalDto;
import com.arthall.modam.service.MusicalService;

@RestController
@RequestMapping("/api/musicals")
public class MusicalController {

    @Autowired
    private MusicalService musicalService;

    @GetMapping
    public List<MusicalDto> getAllMusicals() {
        return musicalService.getAllMusicals();
    }

    @GetMapping("/{musicalId}")
    public MusicalDto getMusical(@PathVariable int musicalId) {
        return musicalService.getMusical(musicalId);
    }
}

