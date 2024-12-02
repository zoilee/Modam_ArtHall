package com.arthall.modam.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arthall.modam.entity.ShowEntity;
import com.arthall.modam.service.ShowService;

@Controller
public class UtilController {

    @Autowired
    private ShowService showService;

    
    @GetMapping("/getShowId")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getShowId(
        @RequestParam("performanceId") int performanceId,
        @RequestParam("showDate") String showDate,
        @RequestParam("showTime") int showTime) {
        
        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(showDate);
            
            List<ShowEntity> shows = showService.findByPerformanceIdAndShowDateAndShowTime(
                performanceId, 
                sqlDate, 
                showTime
            );
            
            Map<String, Object> response = new HashMap<>();
            
            if (!shows.isEmpty()) {
                response.put("showId", shows.get(0).getId());
            } else {
                response.put("showId", null);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/getSeatAvailability")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSeatAvailability(
        @RequestParam("showId") int showId) {
        
        try {
            ShowEntity show = showService.findById(showId);
            Map<String, Object> response = new HashMap<>();
            
            if (show != null) {
                response.put("seatAvailable", show.getSeatAvailable());
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Show not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

