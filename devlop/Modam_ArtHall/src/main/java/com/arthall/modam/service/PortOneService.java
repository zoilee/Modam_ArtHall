package com.arthall.modam.service;

import org.springframework.stereotype.Service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.AccessToken;



@Service
public class PortOneService {
    private IamportClient api;
    private final String API_KEY ="3525653010711283";
    private final String API_SECRET = "JPw7v6FxFDnmsWu9D65dg5kb6ffdjlNzG4h6Y5P2rNeBpxZH6Mmj4UfZcgWg44y9DcnpEZkwz9rbatGU";

    public PortOneService(){
        this.api = new IamportClient(API_KEY, API_SECRET);
    }


}
