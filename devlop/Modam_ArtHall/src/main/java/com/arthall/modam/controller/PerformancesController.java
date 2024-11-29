package com.arthall.modam.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.service.PerformanceService;
@RestController
@RequestMapping("/showListFragment")
public class PerformancesController {
    @Autowired
    private PerformanceService performanceService;
    @GetMapping
    public Page<PerformancesEntity> getPastPerformances(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        return performanceService.getPastPerformances(PageRequest.of(page, size));
    }
}
