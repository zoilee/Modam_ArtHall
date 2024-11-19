package com.arthall.modam.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoOAuthService {
private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        var headers = new org.springframework.http.HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        var request = new org.springframework.http.HttpEntity<>(headers);
        var response = restTemplate.postForEntity(url, request, Map.class);

        return response.getBody();
    }
}
