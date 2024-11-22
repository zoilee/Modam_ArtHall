package com.arthall.modam.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.entity.ReservationsEntity;
import com.arthall.modam.entity.ShowEntity;
import com.arthall.modam.repository.ShowRepository;
import com.arthall.modam.service.PerformanceService;
import com.arthall.modam.service.ReservationsService;

@Controller
public class ReservationController {
    @Autowired
    private PerformanceService performanceService; // 서비스 클래스에서 공연 정보를 조회

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ReservationsService reservationService;

     // 예약 정보 저장
    @PostMapping("/reservConfirm")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmReservation(@RequestBody ReservationsEntity reservationEntity) {

        // 예약 처리 로직
        ReservationsEntity savedReservation = reservationService.createReservation(reservationEntity);

        Map<String, Object> response = new HashMap<>();
        if (savedReservation != null) {
            response.put("success", true);
            response.put("data", savedReservation);
        } else {
            response.put("success", false);
            response.put("message", "예약 처리에 실패했습니다.");
        }

        return ResponseEntity.ok(response);
    }
}

