package com.arthall.modam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReservationController {
    @PostMapping("/modam/reservConfirm")
    public String handleReservation(
            @RequestParam("musicalId") int musicalId,
            @RequestParam("selectedDate") String selectedDate,
            @RequestParam("selectedTime") int selectedTime,
            @RequestParam("numberOfPeople") int numberOfPeople,
            @RequestParam("selectedSeats1") String selectedSeats1,
            @RequestParam("selectedSeats2") String selectedSeats2) {
        
        // 받은 값들을 사용하여 필요한 처리 (예: 예약 정보 저장)
        
        // 예시로 받은 값들을 출력
        System.out.println("Musical ID: " + musicalId);
        System.out.println("Selected Date: " + selectedDate);
        System.out.println("Selected Time: " + selectedTime);
        System.out.println("Number of People: " + numberOfPeople);
        System.out.println("Selected Seats 1: " + selectedSeats1);
        System.out.println("Selected Seats 2: " + selectedSeats2);

        // 처리 후, 예약 완료 페이지나 다른 페이지로 리다이렉트하거나 화면을 반환
        return "reservationConfirmationPage";
    }
}

