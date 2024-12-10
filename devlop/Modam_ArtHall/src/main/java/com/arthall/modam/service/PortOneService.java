package com.arthall.modam.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.arthall.modam.repository.PaymentsRepository;
import com.arthall.modam.repository.ReservationsRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PortOneService {
    private IamportClient iamportClient;
    private ReservationsRepository reservationsRepository;
    @Autowired
    private PaymentsRepository paymentsRepository;

    @Value("${api.key}")
    private String apiKey;

    @Value("${api.secret}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    public IamportResponse<Payment> getRefundByImpuid(CancelData cancelData) throws Exception {
        log.info("포트원 환불 정보 조회");
        return iamportClient.cancelPaymentByImpUid(cancelData);
    }

    public IamportResponse<Payment> getPaymentByImpUid(String impUid) throws Exception {
        log.info("포트원 결제 정보 조회 - imp_uid: {}", impUid);
        return iamportClient.paymentByImpUid(impUid);
    }

    public String findImpUidByMerchantUid(int id) {
        String ticket = reservationsRepository.findTicketById(id);
        return ticket;
    }

    /*********************************관리자 대시보드 모달 창 시작*********************************/
    // 오늘의 결제 금액
    public double getTodayPayments() {
        Double total = paymentsRepository.findTodayPayments();
        return total != null ? total : 0.0;
    }

    // 오늘의 환불 금액
    public double getTodayRefunds() {
        Double total = paymentsRepository.findTodayRefunds();
        return total != null ? total : 0.0;
    }

    // 오늘의 적립금 사용 금액
    public double getTodayCreditsUsed() {
        Double total = paymentsRepository.findTodayCreditsUsed();
        return total != null ? total : 0.0;
    }

    // 총 결제 금액
    public double getTotalPayments() {
        Double total = paymentsRepository.findTotalPayments();
        return total != null ? total : 0.0;
    }

    // 총 환불 금액
    public double getTotalRefunds() {
        Double total = paymentsRepository.findTotalRefunds();
        return total != null ? total : 0.0;
    }

    // 총 적립금 사용 금액
    public double getTotalCreditsUsed() {
        Double total = paymentsRepository.findTotalCreditsUsed();
        return total != null ? total : 0.0;
    }
    /*********************************관리자 대시보드 모달 창 끝*********************************/
}
