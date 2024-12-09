package com.arthall.modam.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import com.arthall.modam.service.PerformanceService;

@RestController
@RequestMapping("/showListFragment")
public class PerformancesController {

    @Autowired
    private PerformanceService performanceService;

    @GetMapping
    public Page<Map<String, Object>> getPastPerformances(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        // Service에서 가공된 데이터 반환
        return performanceService.getPastPerformances(PageRequest.of(page, size));
    }
}
