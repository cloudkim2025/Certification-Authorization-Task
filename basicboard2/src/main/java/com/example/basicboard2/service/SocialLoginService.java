package com.example.basicboard2.service;

import com.example.basicboard2.dto.NaverUserResponse;
import com.example.basicboard2.dto.SocialLoginRequestDTO;
import com.example.basicboard2.dto.SocialLoginResponseDTO;
import com.example.basicboard2.model.Member;
import com.example.basicboard2.type.Role;
import com.example.basicboard2.type.SocialLoginType;
import com.example.basicboard2.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final NaverAuthService naverAuthService;
    private final TokenProvider tokenProvider;

    public SocialLoginResponseDTO login(SocialLoginRequestDTO requestDTO) {
        SocialLoginType type = requestDTO.getSocialLoginType();
        String accessToken = requestDTO.getAccessToken();

        log.info("SocialLoginService.login() - socialLoginType: {}, accessToken: {}", type, accessToken);

        switch (type) {
            case NAVER:
                return handleNaverLogin(accessToken);
            // 이후 Apple, Google, Facebook, Kakao 등 추가 예정
            default:
                throw new IllegalArgumentException("지원하지 않는 소셜 로그인 타입");
        }
    }

    private SocialLoginResponseDTO handleNaverLogin(String accessToken) {
        // 네이버 API를 호출하여 사용자 정보 가져오기
        NaverUserResponse naverUserResponse = naverAuthService.getNaverUserInfo(accessToken);
        log.info("handleNaverLogin() - 네이버 유저 정보: {}", naverUserResponse);

        // 네이버 응답을 공통 Member 객체로 매핑 (여기서 nickname 필드에 네이버 닉네임을 저장)
        Member member = Member.builder()
                .id(0L) // DB ID는 나중에 업데이트
                .userId(naverUserResponse.getResponse().getId())
                .userName(naverUserResponse.getResponse().getNickname())  // userName은 본래 닉네임 역할
                .password("") // 소셜 로그인은 비밀번호 사용 안 함
                .role(Role.ROLE_USER)
                .build();

        // 내부 JWT 토큰 발급
        String jwtToken = tokenProvider.generateToken(member, Duration.ofHours(2));
        log.info("handleNaverLogin() - 내부 JWT 토큰 생성됨: {}", jwtToken);

        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new RuntimeException("JWT 토큰 생성 실패");
        }


        return SocialLoginResponseDTO.builder()
                .userId(member.getUserId())
                .email(naverUserResponse.getResponse().getEmail())
                .nickname(member.getUserName())
                .token(jwtToken)
                .build();
    }


}
