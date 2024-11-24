package com.arthall.modam.controller;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.arthall.modam.dto.MailDto;
import com.arthall.modam.entity.PaymentsEntity;
import com.arthall.modam.entity.ReservationsEntity;
import com.arthall.modam.entity.ShowEntity;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.repository.PaymentsRepository;
import com.arthall.modam.repository.ReservationsRepository;
import com.arthall.modam.repository.RewardsRepository;
import com.arthall.modam.repository.ShowRepository;
import com.arthall.modam.repository.UserRepository;
import com.arthall.modam.service.MailService;
import com.arthall.modam.service.PortOneService;
import com.arthall.modam.service.RewardsService;
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
    @Autowired
    MailService mailService;
    @Autowired
    ShowRepository showRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RewardsRepository rewardsRepository;
    @Autowired
    RewardsService rewardsService;
    
    @Transactional
    @PostMapping("/payments/process")
    public ResponseEntity<String> handlePayment(@RequestBody Map<String, Object> paymentData) {

        try {
            String impUid = paymentData.get("imp_uid").toString(); // 포트원에서 발급하는 고유 번호
            // impUid로 결제 정보 조회
            IamportResponse<Payment> payment = portOneService.getPaymentByImpUid(impUid);

            // 결제 응답 코드가 0이면 결제 성공, 아니면 실패 처리
            if (payment.getCode() == 0) {
                log.info("결제 성공: 주문 번호 - {} 금액 - {}", payment.getResponse().getMerchantUid(),
                        payment.getResponse().getAmount());

                // 결제 성공시db저장
                String ticket = payment.getResponse().getMerchantUid();
                String email = payment.getResponse().getBuyerEmail();
                BigDecimal total_price = payment.getResponse().getAmount(); //결제 가격
                BigDecimal usedPoints = new BigDecimal(paymentData.get("myPoint").toString()); //사용한 적립금
                int userId = Integer.parseInt(paymentData.get("user_id").toString());
                // 예약정보 db
                ReservationsEntity newReservation = new ReservationsEntity();
                ShowEntity showEntity = showRepository.findById(Integer.parseInt(paymentData.get("show_id").toString()))
                                      .orElseThrow(() -> new IllegalArgumentException("Invalid show ID"));
                UserEntity userEntity = userRepository.findById(Integer.parseInt(paymentData.get("user_id").toString()))
                                      .orElseThrow(() -> new IllegalArgumentException("Invalid show ID"));

                

                newReservation.setSeatId1(paymentData.get("seat_id1").toString()); // 좌석id1
                newReservation.setSeatId2(paymentData.get("seat_id2").toString()); // 좌석id2
                newReservation.setShowEntity(showEntity); // 쇼id
                newReservation.setStatus("confirmed"); // 상태
                newReservation.setTotalPrice(total_price); // 가격
                newReservation.setUserEntity(userEntity); // 예약한유저
                newReservation.setTicket(ticket); // 티켓

                reservationsRepository.save(newReservation);
                System.out.println("예약정보 db 저장 성공");
                // 결제정보 db

                ReservationsEntity thisReservation = reservationsRepository.findById(newReservation.getId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Invalid reservation ID: " + newReservation.getId()));
                PaymentsEntity paymentsEntity = new PaymentsEntity();
                paymentsEntity.setAmount(total_price);
                paymentsEntity.setMethod(payment.getResponse().getPayMethod());
                paymentsEntity.setStatus(payment.getResponse().getStatus());
                paymentsEntity.setTransactionType(PaymentsEntity.TransactionType.PAYMENT);
                paymentsEntity.setReservation(thisReservation);

                paymentsRepository.save(paymentsEntity);
                System.out.println("결제정보db 저장 성공");
                Integer reservationId = newReservation.getId(); // 바로 ID 값 가져오기
                System.out.println("Reservation ID: " + reservationId);

                // 적립금 차감 처리
                if (usedPoints.compareTo(BigDecimal.ZERO) > 0) {
                    rewardsService.deductPoints(userId, usedPoints, reservationId );
                }

                // 적립금 계산 및 적립
                if (total_price.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal rewardPoints = total_price.multiply(BigDecimal.valueOf(0.03)); // 3% 적립
                    rewardsService.addPoints(userId, rewardPoints, "EARN", reservationId);
                    log.info("결제 금액 {}원 기준으로 적립금 {}원 추가", total_price, rewardPoints);
                } else {
                    log.info("결제 금액이 0원이므로 적립금을 추가하지 않습니다.");
                }


                // 이메일로 티켓번호 보내주기
                MailDto mailDto = new MailDto();
                mailDto.setAddress(email);
                mailDto.setMessage("<p>고객님의 티켓번호는 <strong>" + ticket + "</strong> 입니다</p>");
                mailDto.setTitle("ModamArtHall : 예약해주셔서 감사합니다.");

                mailService.mailSend(mailDto);
                System.out.println("메일 전송 성공");

                return ResponseEntity.ok("결제 성공");

            } else {
                log.error("결제 실패: {}", payment.getMessage());
                return ResponseEntity.status(400).body("결제 실패");
            }
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(500).body("결제 처리 중 오류 발생");
        }
    }

}