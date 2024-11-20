package com.arthall.modam.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
@Slf4j
public class PaymentController {

    private IamportClient iamportClient;
 
    @Value("${api.key}")
    private String apiKey;
 
    @Value("${api.secret}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    @PostMapping("/payments/process")
    public ResponseEntity<String> handlePayment(@RequestBody Map<String, Object> paymentData) {
        
        try {
            String impUid = paymentData.get("imp_uid").toString();  // 주문 번호
            // impUid로 결제 정보 조회
            System.out.println(impUid);
            IamportResponse<Payment> payment = iamportClient.paymentByImpUid(impUid);

            // 결제 응답 코드가 0이면 결제 성공, 아니면 실패 처리
            if (payment.getCode() == 0) {
                log.info("결제 성공: 주문 번호 - {} 금액 - {}", payment.getResponse().getMerchantUid(), payment.getResponse().getAmount());
                return ResponseEntity.ok("결제 성공");
            } else {
                log.error("결제 실패: {}", payment.getMessage());
                return ResponseEntity.status(400).body("결제 실패");
            }
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생: {}", e.getMessage());
            e.printStackTrace();  // 전체 스택 트레이스 출력
            return ResponseEntity.status(500).body("결제 처리 중 오류 발생");
        }
    }
}