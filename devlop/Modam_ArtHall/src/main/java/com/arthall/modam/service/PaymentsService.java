package com.arthall.modam.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.arthall.modam.entity.PaymentsEntity;
import com.arthall.modam.entity.ReservationsEntity;
import com.arthall.modam.repository.PaymentsRepository;
import com.siot.IamportRestClient.response.Payment;

import jakarta.transaction.Transactional;

public class PaymentsService {
    @Autowired
    PaymentsRepository paymentsRepository;

    public void savePayments(PaymentsEntity paymentsEntity) {
        paymentsRepository.save(paymentsEntity);
    }

    public PaymentsEntity findPaymentsByReservation(ReservationsEntity reservation) {
        return paymentsRepository.findByReservation(reservation);
    }

    // @Transactional
    // public Map<String, Object> processPayment(Map<String, Object> paymentData) {
    // Map<String, Object> response = new HashMap<>();
    // try {
    // // 1. 결제 검증
    // Payment payment = validatePayment(paymentData);

    // // 2. 예약 생성
    // ReservationsEntity reservation = createReservation(paymentData, payment);

    // // 3. 결제 데이터 저장
    // savePaymentData(payment, reservation);

    // // 4. 적립금 처리
    // handleRewards(paymentData, reservation);

    // // 5. 알림 생성
    // sendNotifications(reservation);

    // response.put("message", "결제 성공");
    // } catch (Exception e) {
    // throw new RuntimeException("결제 처리 중 오류 발생", e);
    // }
    // return response;
    // }

    // private Payment validatePayment(Map<String, Object> paymentData) {
    // // 결제 데이터 검증
    // }

    // private ReservationsEntity createReservation(Map<String, Object> paymentData,
    // Payment payment) {
    // // 예약 생성
    // }

    // private void savePaymentData(Payment payment, ReservationsEntity reservation)
    // {
    // // 결제 정보 저장
    // }

    // private void handleRewards(Map<String, Object> paymentData,
    // ReservationsEntity reservation) {
    // // 적립금 처리
    // }

    // private void sendNotifications(ReservationsEntity reservation) {
    // // 알림 전송
    // }
}
