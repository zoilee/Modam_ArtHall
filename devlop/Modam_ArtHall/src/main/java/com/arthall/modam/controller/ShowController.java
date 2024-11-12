package com.arthall.modam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arthall.modam.dto.ShowDto;
import com.arthall.modam.service.ShowService;



@RestController
@RequestMapping("/api/shows")
public class ShowController {

    @Autowired
    private ShowService showService;

    @GetMapping("/{musicalId}")
    public List<ShowDto> getShowsByMusical(@PathVariable int musicalId) {
        return showService.getShowsByMusical(musicalId);
    }
}

