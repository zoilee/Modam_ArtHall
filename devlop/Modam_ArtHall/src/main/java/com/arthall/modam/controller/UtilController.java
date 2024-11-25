package com.arthall.modam.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.arthall.modam.entity.ShowEntity;
import com.arthall.modam.repository.ShowRepository;

@Controller
public class UtilController {

    @Autowired
    private ShowRepository showRepository;

    
    @GetMapping("/getShowId")
    public ResponseEntity<Map<String, Object>> getShowId(@RequestParam("performanceId") int performanceId,
                                                          @RequestParam("showDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date showDate,
                                                          @RequestParam("showTime") int showTime) {
        List<ShowEntity> shows = showRepository.findByPerformancesEntity_IdAndShowDateAndShowTime(performanceId, (java.sql.Date) showDate, showTime);
        
        Map<String, Object> response = new HashMap<>();
        if (!shows.isEmpty()) {
            response.put("showId", shows.get(0).getShowId()); // 첫 번째 결과를 사용
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "해당 공연 정보를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}

