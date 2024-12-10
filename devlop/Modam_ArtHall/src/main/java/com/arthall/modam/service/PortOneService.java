package com.arthall.modam.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    // 오늘의 매출 데이터
    public Map<String, Double> getTodaySales() {
        Double todayPayments = paymentsRepository.findTodayPayments();
        Double todayRefunds = paymentsRepository.findTodayRefunds();
        Double todayCreditsUsed = paymentsRepository.findTodayCreditsUsed();
        Double todayNetSales = todayPayments - todayRefunds - todayCreditsUsed;

        Map<String, Double> todaySales = new HashMap<>();
        todaySales.put("todayPayments", todayPayments);
        todaySales.put("todayRefunds", todayRefunds);
        todaySales.put("todayCreditsUsed", todayCreditsUsed);
        todaySales.put("todayNetSales", todayNetSales);

        return todaySales;
    }

    // 특정 달의 매출 데이터
    public Map<String, Double> getSalesForMonth(int year, int month) {
        Double monthlyPayments = paymentsRepository.findMonthlyPayments(year, month);
        Double monthlyRefunds = paymentsRepository.findMonthlyRefunds(year, month);
        Double monthlyCreditsUsed = paymentsRepository.findMonthlyCreditsUsed(year, month);
        Double monthlyNetSales = monthlyPayments - monthlyRefunds - monthlyCreditsUsed;

        Map<String, Double> monthlySales = new HashMap<>();
        monthlySales.put("monthlyPayments", monthlyPayments);
        monthlySales.put("monthlyRefunds", monthlyRefunds);
        monthlySales.put("monthlyCreditsUsed", monthlyCreditsUsed);
        monthlySales.put("monthlyNetSales", monthlyNetSales);

        return monthlySales;
    /*********************************관리자 대시보드 모달 창 끝*********************************/
    }
}