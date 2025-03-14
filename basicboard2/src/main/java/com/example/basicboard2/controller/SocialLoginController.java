package com.example.basicboard2.controller;

import com.example.basicboard2.config.jwt.TokenProvider;
import com.example.basicboard2.dto.NaverUserResponse;
import com.example.basicboard2.model.Member;
import com.example.basicboard2.service.NaverAuthService;
import com.example.basicboard2.type.Role;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/social")
public class SocialLoginController {

    private final NaverAuthService naverAuthService;
    private final TokenProvider tokenProvider; // 내부 JWT 생성을 위한 TokenProvider

    /**
     * 네이버 로그인 페이지로 리다이렉트 (CSRF 방지를 위해 상태 토큰 포함)
     */
    @GetMapping("/naver")
    public void redirectToNaver(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 상태 토큰 생성 및 세션 저장 (CSRF 방지)
        String state = UUID.randomUUID().toString();
        request.getSession().setAttribute("naver_oauth_state", state);

        // 네이버 로그인 URL 생성
        String authorizeUrl = naverAuthService.buildAuthorizeUrl(state);
        log.info("Redirecting to Naver URL: {}", authorizeUrl);

        // 네이버 로그인 페이지로 리다이렉트
        response.sendRedirect(authorizeUrl);
    }

    /**
     * 네이버 로그인 콜백 핸들러
     */
    @GetMapping("/login/callback")
    public void handleNaverCallback(@RequestParam("code") String code,
                                    @RequestParam("state") String state,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws IOException {
        String sessionState = (String) request.getSession().getAttribute("naver_oauth_state");
        if (sessionState == null || !sessionState.equals(state)) {
            log.error("CSRF 검증 실패 - state 불일치");
            response.sendRedirect("/access-denied");
            return;
        }

        // 네이버 OAuth 2.0 액세스 토큰 요청
        String tokenResponse = naverAuthService.requestAccessToken(code, state);
        String naverAccessToken = extractAccessToken(tokenResponse);
        if (naverAccessToken == null || naverAccessToken.isEmpty()) {
            log.error("네이버 액세스 토큰 추출 실패");
            response.sendRedirect("/access-denied");
            return;
        }
        log.info("네이버 access_token: {}", naverAccessToken);

        // 네이버 사용자 정보 가져오기
        NaverUserResponse naverUserResponse = naverAuthService.getNaverUserInfo(naverAccessToken);

        // 네이버 사용자 정보를 내부 Member 객체로 변환
        Member member = Member.builder()
                .id(0L) // 실제 DB ID는 추후 업데이트
                .userId(naverUserResponse.getResponse().getId())
                .userName(naverUserResponse.getResponse().getNickname())
                .password("") // 소셜 로그인은 비밀번호 사용 안 함
                .role(Role.ROLE_USER)
                .build();

        // 내부 JWT 생성
        String jwtToken = tokenProvider.generateToken(member, Duration.ofHours(2));
        log.info("JWT 생성 완료: {}", jwtToken);

        // 클라이언트에 내부 JWT 전달 (정적 콜백 페이지로 리다이렉트)
        response.sendRedirect("/naver-callback.html?access_token="
                + URLEncoder.encode(jwtToken, StandardCharsets.UTF_8));
    }

    /**
     * JSON 응답에서 네이버 access_token 값을 추출하는 메서드 (Jackson 사용)
     */
    private String extractAccessToken(String tokenResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(tokenResponse);
            String accessToken = node.path("access_token").asText();
            if (accessToken == null || accessToken.isEmpty()) {
                log.error("네이버 응답에서 access_token을 찾을 수 없음");
                return null;
            }
            return accessToken;
        } catch (Exception e) {
            log.error("네이버 액세스 토큰 파싱 실패: {}", e.getMessage());
            return null;
        }
    }
}
