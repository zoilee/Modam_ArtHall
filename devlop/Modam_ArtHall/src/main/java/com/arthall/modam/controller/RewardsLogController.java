package com.arthall.modam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arthall.modam.entity.RewardsLogEntity;
import com.arthall.modam.service.RewardsLogService;

import java.util.List;

@RestController
@RequestMapping("/rewards-log")
public class RewardsLogController {

    @Autowired
    private RewardsLogService rewardsLogService;

    @GetMapping
    public ResponseEntity<List<RewardsLogEntity>> getLogsByUserIdAndReservationId(
            @RequestParam("userId") int userId,
            @RequestParam("reservationsId") int reservationsId) {

        List<RewardsLogEntity> logs = rewardsLogService.getLogsByUserIdAndReservationId(userId, reservationsId);

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build(); // 데이터가 없을 경우
        }

        return ResponseEntity.ok(logs); // 데이터가 있을 경우 JSON 반환
    }
}