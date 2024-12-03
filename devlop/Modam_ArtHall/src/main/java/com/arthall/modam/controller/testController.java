package com.arthall.modam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.arthall.modam.service.PortOneService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.PaymentBalance;

import java.util.Map;



@RestController
@RequestMapping("/webhook")
public class testController {

    @Autowired
    PortOneService portOneService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        // 아임포트에서 받은 데이터 로그 출력
        System.out.println("아임포트 웹훅 호출 데이터: " + payload);


       
        // Discord 메시지 내용 생성
        String impUid = (String) payload.get("imp_uid");
        String status = (String) payload.get("status");
        String merchantUid = (String) payload.get("merchant_uid");
        Integer amount = null;

        try {
            // 결제 정보 조회
            IamportResponse<Payment> paymentResponse = portOneService.getPaymentByImpUid(impUid);
            if (paymentResponse != null && paymentResponse.getResponse() != null) {
                amount = paymentResponse.getResponse().getAmount().intValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("아임포트 결제 조회 실패: " + e.getMessage());
        }



        String content = "결제 상태: " + status + "\n"
                       + "주문 번호: " + merchantUid + "\n"
                       + "결제 금액: " + amount + "원";

        // Discord 웹훅 URL
        String discordWebhookUrl = "https://discord.com/api/webhooks/1312978001348001873/VcsrGBuGyvySN7-iDJk8Yzu7oTXiFmpaNGDbLMm7ccfZPBuR7L89OvGQw4Cr_xzGAj5B";

        // Discord로 전송할 데이터 생성
        Map<String, String> discordPayload = Map.of("content", content);

        // Discord 웹훅 호출
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForEntity(discordWebhookUrl, discordPayload, String.class);
            return ResponseEntity.ok("Discord 웹훅 전송 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Discord 웹훅 전송 실패: " + e.getMessage());
        }
    }
}