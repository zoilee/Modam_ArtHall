package com.arthall.modam.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PortOneService {
    private IamportClient iamportClient;

    @Value("${api.key}")
    private String apiKey;

    @Value("${api.secret}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    public IamportResponse<Payment> getPaymentByImpUid(String impUid) throws Exception {
        log.info("포트원 결제 정보 조회 - imp_uid: {}", impUid);
        return iamportClient.paymentByImpUid(impUid);
    }

}
