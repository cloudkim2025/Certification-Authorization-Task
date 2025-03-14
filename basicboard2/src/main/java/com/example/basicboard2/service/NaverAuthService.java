package com.example.basicboard2.service;

import com.example.basicboard2.dto.NaverUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverAuthService {

    private final RestTemplate restTemplate;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Value("${naver.redirect.uri}")
    private String redirectUri;

    public String buildAuthorizeUrl(String state) {
        log.info("clientId: {}, redirectUri: {}, state: {}", clientId, redirectUri, state);
        return "https://nid.naver.com/oauth2.0/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&state=" + state;
    }
    // 네이버 API를 통해 사용자 정보를 가져오는 메서드 예시
    public NaverUserResponse getNaverUserInfo(String accessToken) {
        String url = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<NaverUserResponse> responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, entity, NaverUserResponse.class
        );

        NaverUserResponse response = responseEntity.getBody();

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(response);
            log.info("네이버 전체 응답 데이터: {}", json);
        } catch (Exception e) {
            log.error("네이버 응답 데이터를 JSON으로 변환 실패: {}", e.getMessage());
        }

        return response;
    }

    public String requestAccessToken(String code, String state) {
        String tokenUrl = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&code=" + code
                + "&state=" + state;

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                tokenUrl, HttpMethod.GET, null, String.class
        );
        String responseBody = responseEntity.getBody();
        log.info("토큰 응답 데이터: {}", responseBody);

        return responseBody;
    }
}
