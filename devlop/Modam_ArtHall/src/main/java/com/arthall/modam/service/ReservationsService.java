package com.arthall.modam.service;

import java.math.RoundingMode;
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
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.repository.PaymentsRepository;
import com.arthall.modam.repository.ReservationsRepository;
import com.arthall.modam.repository.UserRepository;

@Service
public class ReservationsService {
    @Autowired
    private ReservationsRepository reservationRepository;

    @Autowired
    private PaymentsRepository paymentsRepository;

    @Autowired
    private UserRepository userRepository;

    public void saveReservation(ReservationsEntity reservation) {
        reservationRepository.save(reservation);
    }

    // 인터페이스 처리해야할듯?
    public ReservationsEntity findById(ReservationsEntity reservation) {
        return reservationRepository.findById(reservation.getId()).orElseThrow(() -> new IllegalArgumentException(
                "Invalid reservation ID: " + reservation.getId()));
    }

    public ReservationsEntity findById(int id) {
        return reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid res ID"));
    }

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

    // 전체 예약 조회
    public List<ReservationsEntity> getAllReservations() {
        return reservationRepository.findAll();
    }

    // 전화번호나 티켓번호를 기반으로 예약을 조회하는 메소드
    public List<ReservationsEntity> getReservationsByPhoneOrTicket(String phoneNumber, String ticket) {
        // 전화번호와 티켓번호가 모두 null이 아닌 경우
        if (phoneNumber != null && !phoneNumber.isEmpty() && ticket != null && !ticket.isEmpty()) {
            return reservationRepository.findByUserEntity_PhoneNumberContainingAndTicketContaining(phoneNumber, ticket);
        }
        // 전화번호만 있는 경우
        else if (phoneNumber != null && !phoneNumber.isEmpty()) {
            return reservationRepository.findByUserEntity_PhoneNumberContaining(phoneNumber);
        }
        // 티켓번호만 있는 경우
        else if (ticket != null && !ticket.isEmpty()) {
            return reservationRepository.findByTicketContaining(ticket);
        }
        // 전화번호와 티켓번호가 모두 null인 경우 전체 예약을 반환
        else {
            return reservationRepository.findAll();
        }
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

    public List<ReservationsEntity> getTodayConfirmedReservations() {
        List<ReservationsEntity> todayReservations = reservationRepository.findTodayConfirmedReservations();

        // 금액 포맷팅
        for (ReservationsEntity reservation : todayReservations) {
            if (reservation.getTotalPrice() != null) {
                reservation.setFormattedTotalPrice(
                        "₩" + reservation.getTotalPrice().setScale(0, RoundingMode.HALF_UP).toString());
            } else {
                reservation.setFormattedTotalPrice("₩0");
            }
        }
        return todayReservations;
    }

    // 이름이랑 전화번호로 공연찾기
    public List<ReservationsEntity> getReservByNameAndNum(String name, String num) {

        // 이름이랑 전화번호로 유저찾기
        UserEntity userEntity = userRepository.findByNameAndPhoneNumber(name, num).orElse(null);
        // 유저아이디로 공연찾기
        List<ReservationsEntity> thisReserv = reservationRepository.findByUserId(userEntity.getId());

        // 리턴 공연
        return thisReserv;
    }

}
