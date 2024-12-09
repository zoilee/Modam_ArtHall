package com.arthall.modam.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.ReservationsEntity;
import com.arthall.modam.repository.PaymentsRepository;
import com.arthall.modam.repository.ReservationsRepository;

@Service
public class ReservationsService {
    @Autowired
    private ReservationsRepository reservationRepository;

    @Autowired
    private PaymentsRepository paymentsRepository;

    // 향후 예약 조회
    public List<ReservationsEntity> getUpcomingReservations(int userId) {
        LocalDate today = LocalDate.now();
        return reservationRepository.findUpcomingReservationsByShowDate(userId, today)
                .stream()
                .filter(reservation -> !"CANCEL".equalsIgnoreCase(reservation.getStatus())) // status 필터링
                .collect(Collectors.toList());
    }

    // 과거 예약 조회
    public List<ReservationsEntity> getPastReservations(int userId) {
        LocalDate today = LocalDate.now();
        return reservationRepository.findPastReservationsByShowDate(userId, today)
                .stream()
                .filter(reservation -> !"CANCEL".equalsIgnoreCase(reservation.getStatus())) // status 필터링
                .collect(Collectors.toList());
    }

    public ReservationsEntity createReservation(ReservationsEntity reservationEntity) {
        // 예약을 DB에 저장
        reservationEntity.setReservationDate(new Timestamp(System.currentTimeMillis())); // 예약 날짜 설정
        reservationEntity.setStatus("CONFIRMED"); // 기본 상태 설정

        return reservationRepository.save(reservationEntity); // 저장 후 반환
    }

    // 예약이 완료된 좌석 가져오기
    public List<String> getUnavailableSeats(int showId) {
        List<ReservationsEntity> reservations = reservationRepository.findByShowEntity_IdAndStatus(showId, "confirmed");

        List<String> unavailableSeats = new ArrayList<>();
        for (ReservationsEntity reservation : reservations) {
            if (reservation.getSeatId1() != null) {
                unavailableSeats.add(reservation.getSeatId1());
            }
            if (reservation.getSeatId2() != null) {
                unavailableSeats.add(reservation.getSeatId2());
            }
        }
        return unavailableSeats;
    }

    // 현재 상영 중 또는 미래 공연 중 매출이 있는 데이터
    public List<Map<String, Object>> getPerformancesWithTotalSales() {
        // Repository에서 데이터 가져오기
        List<Object[]> results = paymentsRepository.findPerformancesWithTotalSales();

        // 결과 가공
        return results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("performanceTitle", result[0]); // 공연 제목
            map.put("totalSales", result[1]); // 총 매출
            return map;
        }).collect(Collectors.toList());
    }

    // 최근 7일 동안 예약 현황 데이터 (현재 상영 중 또는 미래 공연)
    public List<Map<String, Object>> getReservationsByShowDate() {
        Date endDate = Date.valueOf(LocalDate.now().plusDays(7));
        List<Object[]> results = reservationRepository.findReservationsByShowDate(endDate);
    
        // 결과를 가공하여 반환
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("showDate", row[0].toString()); // showDate
            map.put("performanceTitle", row[1]); // performanceTitle
            map.put("totalReservations", row[2]); // totalReservations
            return map;
        }).collect(Collectors.toList());
    }

    // 오늘 결제된 예약 목록 가져오기
    public List<ReservationsEntity> getTodayPaidReservations() {
        List<ReservationsEntity> reservations = reservationRepository.findTodayPaidReservations();
        if (reservations == null || reservations.isEmpty()) {
            System.out.println("No reservations found for today.");
        } else {
            System.out.println("Reservations found: " + reservations.size());
        }
        return reservations;
    }
}
