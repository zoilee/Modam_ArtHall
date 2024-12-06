package com.arthall.modam.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.arthall.modam.dto.MailDto;
import com.arthall.modam.entity.AlramEntitiy;
import com.arthall.modam.entity.PaymentsEntity;
import com.arthall.modam.entity.ReservationsEntity;

import com.arthall.modam.entity.RewardsLogEntity;
import com.arthall.modam.entity.ShowEntity;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.repository.AlramRepository;
import com.arthall.modam.repository.PaymentsRepository;
import com.arthall.modam.repository.ReservationsRepository;
import com.arthall.modam.repository.RewardsLogRepository;
import com.arthall.modam.repository.RewardsRepository;
import com.arthall.modam.repository.ShowRepository;
import com.arthall.modam.repository.UserRepository;
import com.arthall.modam.service.AlramService;
import com.arthall.modam.service.MailService;
import com.arthall.modam.service.PerformanceService;
import com.arthall.modam.service.PortOneService;
import com.arthall.modam.service.RewardsService;
import com.siot.IamportRestClient.request.CancelData;
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
    // Repository service로 바꾸기
    @Autowired
    PaymentsRepository paymentsRepository;
    // Repository service로 바꾸기
    @Autowired
    ReservationsRepository reservationsRepository;
    @Autowired
    MailService mailService;
    // Repository service로 바꾸기
    @Autowired
    ShowRepository showRepository;
    @Autowired
    UserRepository userRepository;
    // Repository service로 바꾸기
    @Autowired
    RewardsRepository rewardsRepository;
    @Autowired
    RewardsService rewardsService;

    // Repository service로 바꾸기
    @Autowired
    RewardsLogRepository rewardsLogRepository;
    @Autowired
    AlramService alramService;
    @Autowired
    PerformanceService performanceService;

    @Transactional
    @PostMapping("/payments/process")
    public ResponseEntity<Map<String, Object>> handlePayment(@RequestBody Map<String, Object> paymentData) {
        // 롤백 데이터 맵
        Map<String, Object> response = new HashMap<>();
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
                BigDecimal total_price = payment.getResponse().getAmount(); // 결제 가격
                BigDecimal usedPoints = new BigDecimal(paymentData.get("myPoint").toString()); // 사용한 적립금
                int userId = Integer.parseInt(paymentData.get("user_id").toString());
                // 예약정보 db
                ReservationsEntity newReservation = new ReservationsEntity();
                ShowEntity showEntity = showRepository.findById(Integer.parseInt(paymentData.get("show_id").toString()))
                        .orElseThrow(() -> new IllegalArgumentException("Invalid show ID"));
                UserEntity userEntity = userRepository.findById(Integer.parseInt(paymentData.get("user_id").toString()))
                        .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

                newReservation.setSeatId1(paymentData.get("seat_id1").toString()); // 좌석id1
                newReservation.setSeatId2(paymentData.get("seat_id2").toString()); // 좌석id2
                newReservation.setShowEntity(showEntity); // 쇼id
                newReservation.setStatus("CONFIRMED"); // 상태
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
                paymentsEntity.setReAmount(total_price);
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
                    rewardsService.deductPoints(userId, usedPoints, reservationId, "USE");
                }

                // 적립금 계산 및 적립
                if (total_price.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal rewardPoints = total_price.multiply(BigDecimal.valueOf(0.03)); // 3% 적립
                    rewardsService.addPoints(userId, rewardPoints, "EARN", reservationId);
                    log.info("결제 금액 {}원 기준으로 적립금 {}원 추가", total_price, rewardPoints);
                    String rewardPointStr = rewardPoints.toString();
                    response.put("rewardPoints", rewardPointStr);
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
                response.put("message", "결제 성공");

                // 관리자 알람 보내기

                String alramTitle = showEntity.getPerformancesEntity().getTitle();
                String alramDate = showEntity.getShowDate().toString();
                String alramSeat1 = thisReservation.getSeatId1().toString();
                String alramSeat2 = thisReservation.getSeatId2().toString();
                String alramLog = alramDate + " 일자의 " + alramTitle + " " + alramSeat1 + ", " + alramSeat2
                        + " 가 예약되었습니다.";

                AlramEntitiy alramEntitiy = new AlramEntitiy();
                alramEntitiy.setType("PAYMENT");
                alramEntitiy.setLog(alramLog);
                alramService.createdAlram(alramEntitiy);

                return ResponseEntity.ok(response);

            } else {
                log.error("결제 실패: {}", payment.getMessage());
                response.put("message", "결제 실패: " + payment.getMessage());
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생: {}", e.getMessage());
            response.put("message", "결제 처리 중 오류 발생");
            return ResponseEntity.status(500).body(response);
        }
    }

    @Transactional
    @PostMapping("/payments/refund")
    public ResponseEntity<Map<String, Object>> handleRefund(@RequestBody Map<String, Object> refundData) {
        // 롤백 데이터 맵
        Map<String, Object> response = new HashMap<>();
        try {
            // refund 받아온 데이터들
            int resId = Integer.parseInt(refundData.get("id").toString());
            System.out.println("resId : " + resId);
            String merchant_uid = refundData.get("merchant_uid").toString();
            System.out.println("merchant_uid : " + merchant_uid);
            // 환불금액
            BigDecimal cancelAmount = new BigDecimal(
                    Integer.parseInt(refundData.get("cancel_request_amount").toString()));

            // 환불 데이터(ticket 번호로 결제 조회 설정)
            CancelData cancelData = new CancelData(merchant_uid, false, cancelAmount);
            cancelData.setReason("환불요청");

            // 해당 예약
            ReservationsEntity thisReservation = reservationsRepository.findById(resId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid res ID"));

            // 환불 조건
            // 환불체크
            if ("CANCEL".equals(thisReservation.getStatus())) {
                response.put("message", "이미 취소된 예약입니다.");
                return ResponseEntity.ok(response);

            }

            // 패널티 check
            LocalDate localShowDate = thisReservation.getShowEntity().getShowDate().toLocalDate();
            long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), localShowDate);

            IamportResponse<Payment> refund = null;
            if (daysBetween >= 7) {
                // 전액 환불
                refund = portOneService.getRefundByImpuid(cancelData);
            } else if (daysBetween >= 1 && daysBetween <= 6) {
                // 1~6일 50% 패널티 적용
                BigDecimal penaltycancelAmount = cancelAmount.multiply(BigDecimal.valueOf(0.5));
                CancelData cancelPenaltyData = new CancelData(merchant_uid, false, penaltycancelAmount);
                refund = portOneService.getRefundByImpuid(cancelPenaltyData);
                System.out.println("캔슬데이터 확인" + cancelPenaltyData);
                System.out.println("패널티 기간이므로 사용한 적립금은 환불X");
            } else if (daysBetween == 0) {
                // 공연 당일 환불 불가
                response.put("message", "환불 불가: 공연 당일입니다.");
                return ResponseEntity.ok(response);
            } else {
                // 공연 이후 환불 불가
                response.put("message", "환불 불가: 이미 공연이 종료되었습니다");
                return ResponseEntity.ok(response);
            }
            // 환불 데이터로 결제 정보 조회 및 환불

            if (refund.getCode() == 0) {

                // 예약정보 db

                thisReservation.setStatus("CANCEL");
                reservationsRepository.save(thisReservation);

                System.out.println("예약정보 db 캔슬로 저장 성공");

                // 결제정보 db
                PaymentsEntity thispayment = paymentsRepository.findByReservation(thisReservation);

                BigDecimal originalAmount = thispayment.getAmount(); // 결제된 총 금액
                BigDecimal refundedAmount = cancelAmount; // 이번에 환불된 금액
                if (daysBetween >= 1 && daysBetween <= 6) {
                    refundedAmount = cancelAmount.multiply(BigDecimal.valueOf(0.5));
                }
                BigDecimal remainingAmount = originalAmount.subtract(refundedAmount); // 환불 후 남은 금액

                thispayment.setStatus("REFUND");
                thispayment.setReAmount(remainingAmount);
                thispayment.setTransactionType(PaymentsEntity.TransactionType.REFUND);
                paymentsRepository.save(thispayment);
                int userId = thisReservation.getUserEntity().getId();
                // 패널티 없는 환불만
                if (daysBetween >= 7) {
                    // 적립금 db rollbackPoints(int userId, BigDecimal points, int reservationId)
                    Optional<RewardsLogEntity> useRewardsLogOpt = rewardsLogRepository
                            .findByUserIdAndDescriptionAndReservationsId(userId, "USE", resId);
                    if (useRewardsLogOpt.isPresent()) {
                        RewardsLogEntity thisRewardsLog = useRewardsLogOpt.get();
                        BigDecimal rollbackPoints = thisRewardsLog.getChangePoint().abs(); // abs 메서드로 절대값 변환
                        rewardsService.rollbackPoints(userId, rollbackPoints, resId);
                    } else {
                        System.out.println("적립금 사용 로그가 없습니다. userId: " + userId + ", reservationId: " + resId);
                    }
                    // 받은 적립금 cancel
                    RewardsLogEntity cancelRewardsLog = rewardsLogRepository
                            .findByUserIdAndDescriptionAndReservationsId(userId, "EARN", resId)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Invalid reservation ID: " + thisReservation.getId())); // EARN인 로그가져오기
                    BigDecimal cancelPoints = cancelRewardsLog.getChangePoint();
                    rewardsService.deductPoints(userId, cancelPoints, resId, "CANCEL");

                    System.out.println("적립금 db 반환 및 롤백 성공");

                } else {
                    // 받은 적립금 cancel
                    RewardsLogEntity cancelRewardsLog = rewardsLogRepository
                            .findByUserIdAndDescriptionAndReservationsId(userId, "EARN", resId)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Invalid reservation ID: " + thisReservation.getId())); // EARN인 로그가져오기
                    BigDecimal cancelPoints = cancelRewardsLog.getChangePoint();
                    rewardsService.deductPoints(userId, cancelPoints, resId, "CANCEL");

                    System.out.println("적립금 db 반환 및 롤백 성공");
                }

                // 관리자 알람 전송
                String alramTitle = thisReservation.getShowEntity().getPerformancesEntity().getTitle();
                String alramDate = thisReservation.getShowEntity().getShowDate().toString();
                String alramSeat1 = thisReservation.getSeatId1().toString();
                String alramSeat2 = thisReservation.getSeatId2().toString();
                String alramLog = alramDate + " 일자의 " + alramTitle + " " + alramSeat1 + ", " + alramSeat2
                        + " 가 취소되었습니다.";

                AlramEntitiy alramEntitiy = new AlramEntitiy();
                alramEntitiy.setType("REFUND");
                alramEntitiy.setLog(alramLog);
                alramService.createdAlram(alramEntitiy);

                response.put("message", "환불 성공");

                return ResponseEntity.ok(response);
            } else {
                log.error("환불 실패: {}", refund.getMessage());
                response.put("message", "환불 실패: " + refund.getMessage());
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            log.error("환불 처리 중 오류 발생: {}", e.getMessage());
            response.put("message", "환불 처리 중 오류 발생");
            return ResponseEntity.status(500).body(response);
        }
    }
}