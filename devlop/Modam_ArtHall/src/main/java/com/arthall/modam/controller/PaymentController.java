package com.arthall.modam.controller;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.arthall.modam.entity.ImagesEntity;
import com.arthall.modam.entity.PaymentsEntity;
import com.arthall.modam.entity.ReservationsEntity;
import com.arthall.modam.repository.PaymentsRepository;
import com.arthall.modam.repository.ReservationsRepository;
import com.arthall.modam.service.PortOneService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@Slf4j
public class PaymentController {

    @Autowired
    PortOneService portOneService;
    @Autowired
    PaymentsRepository paymentsRepository;
    @Autowired
    ReservationsRepository reservationsRepository;

    @PostMapping("/payments/process")
    public ResponseEntity<String> handlePayment(@RequestBody Map<String, Object> paymentData) {

        try {
            String impUid = paymentData.get("imp_uid").toString(); // 포트원에서 발급하는 고유 번호
            // impUid로 결제 정보 조회
            System.out.println(impUid);
            System.out.println("ticket : " + paymentData.get("merchant_uid"));
            IamportResponse<Payment> payment = portOneService.getPaymentByImpUid(impUid);

            // 결제 응답 코드가 0이면 결제 성공, 아니면 실패 처리
            if (payment.getCode() == 0) {
                log.info("결제 성공: 주문 번호 - {} 금액 - {}", payment.getResponse().getMerchantUid(),
                        payment.getResponse().getAmount());

                // 결제 성공시 db저장
                PaymentsEntity paymentsEntity = new PaymentsEntity();
                BigDecimal total_price = payment.getResponse().getAmount();
                int reservationid = 1;
                ReservationsEntity reservation = reservationsRepository.findById(reservationid)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid reservation ID: " + reservationid));

                // int reservation_id =
                // Integer.parseInt(paymentData.get("reservation_id").toString());

                paymentsEntity.setAmount(total_price);
                paymentsEntity.setMethod(payment.getResponse().getPayMethod());
                paymentsEntity.setStatus(payment.getResponse().getStatus());
                paymentsEntity.setTransactionType(PaymentsEntity.TransactionType.PAYMENT);
                paymentsEntity.setReservation(reservation);

                paymentsRepository.save(paymentsEntity);
                System.out.println("예약정보db 저장 성공");

                return ResponseEntity.ok("결제 성공");
            } else {
                log.error("결제 실패: {}", payment.getMessage());
                return ResponseEntity.status(400).body("결제 실패");
            }
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생: {}", e.getMessage());
            e.printStackTrace(); // 전체 스택 트레이스 출력
            return ResponseEntity.status(500).body("결제 처리 중 오류 발생");
        }
    }

}