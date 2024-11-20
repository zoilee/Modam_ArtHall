package com.arthall.modam.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.entity.ShowEntity;
import com.arthall.modam.repository.ShowRepository;
import com.arthall.modam.service.PerformanceService;

@Controller
public class ReservationController {
    @Autowired
    private PerformanceService performanceService; // 서비스 클래스에서 공연 정보를 조회

    @Autowired
    private ShowRepository showRepository;

    // reservForm 페이지에 공연 정보 출력
    @RequestMapping(value = "/modam/reservForm", method = RequestMethod.POST)
    public String showReservationForm(@RequestParam("performanceId") int performanceId,
                                      @RequestParam("performanceTitle") String performanceTitle,
                                      Model model) {
        
        // musicalId를 통해 공연 정보를 조회
        Optional<PerformancesEntity> performance = performanceService.getPerformanceById(performanceId);
        
        // 공연 정보가 있으면 모델에 추가
        if (performance != null) {
            model.addAttribute("performancesEntity", performance); // 공연 정보 모델에 추가
        } else {
            // 공연 정보가 없다면 예외 처리 또는 오류 메시지 처리
            model.addAttribute("errorMessage", "해당 공연 정보를 찾을 수 없습니다.");
        }

        return "reservForm";
    }

    //title, date, time으로 show id 검색
    @RequestMapping("/reservForm")
    public String getShowDetails(@RequestParam("performanceId") int performanceId,
                                 @RequestParam("showDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate showDate,
                                 @RequestParam("showTime") int showTime,
                                 Model model) {
        
        // 주어진 조건으로 ShowEntity를 검색
        List<ShowEntity> shows = showRepository.findByPerformanceIdAndShowDateAndShowTime(performanceId, showDate, showTime);
        
        // 해당 조건으로 검색된 ShowEntity가 있으면 첫 번째 결과를 사용
        if (!shows.isEmpty()) {
            ShowEntity show = shows.get(0); // 첫 번째 결과를 사용
            model.addAttribute("showId", show.getShowId()); // showId를 모델에 추가
        } else {
            model.addAttribute("error", "해당 공연 정보가 없습니다.");
        }
        
        return "reservForm";
    }


    @PostMapping("/modam/reservConfirm")
    public String handleReservation(
            @RequestParam("performanceId") int performanceId,
            @RequestParam("showeId") int showId,
            @RequestParam("selectedDate") String selectedDate,
            @RequestParam("selectedTime") int selectedTime,
            @RequestParam("numberOfPeople") int numberOfPeople,
            @RequestParam("selectedSeats1") String selectedSeats1,
            @RequestParam("selectedSeats2") String selectedSeats2) {
        
        // 받은 값들을 사용하여 필요한 처리 (예: 예약 정보 저장)
        
        // 예시로 받은 값들을 출력
        System.out.println("Performance ID: " + performanceId);
        System.out.println("Show ID: " + showId);
        System.out.println("Selected Date: " + selectedDate);
        System.out.println("Selected Time: " + selectedTime);
        System.out.println("Number of People: " + numberOfPeople);
        System.out.println("Selected Seats 1: " + selectedSeats1);
        System.out.println("Selected Seats 2: " + selectedSeats2);

        
        return "main";
    }
}

