package com.example.basicboard2.controller;

import com.example.basicboard2.config.jwt.TokenProvider;
import com.example.basicboard2.config.security.CustomUserDetails;
import com.example.basicboard2.dto.*;
import com.example.basicboard2.model.Member;
import com.example.basicboard2.service.MemberService;
import com.example.basicboard2.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * ✅ 회원 관련 API 요청을 처리하는 컨트롤러.
 * - 회원 가입, 로그인, 로그아웃 기능을 제공.
 */
@RestController // REST API 컨트롤러 선언
@RequiredArgsConstructor // final 필드를 포함한 생성자를 자동 생성
public class MemberApiController {

    private final MemberService memberService; // 회원 서비스 (비즈니스 로직)
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 비밀번호 암호화 객체
    private final AuthenticationManager authenticationManager; // Spring Security 인증 관리자
    private final TokenProvider tokenProvider; // JWT 토큰 생성 및 검증

    /**
     * ✅ 회원 가입 요청을 처리하는 API.
     * @param signUpRequestDTO 회원 가입 요청 데이터 (JSON)
     * @return 회원 가입 성공 여부를 담은 응답 DTO
     */
    @PostMapping("/join")
    public SignUpResponseDTO signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        System.out.println(signUpRequestDTO); // 요청 데이터 로그 출력

        // 회원 가입 요청 DTO를 Member 엔터티로 변환 후 저장
        memberService.signUp(signUpRequestDTO.toMember(bCryptPasswordEncoder));

        return SignUpResponseDTO.builder()
                .successed(true) // 회원 가입 성공 여부 반환
                .build();
    }

    /**
     * ✅ 로그인 요청을 처리하는 API.
     * - 사용자의 아이디와 비밀번호를 검증하고 JWT 토큰을 발급.
     * - Refresh Token을 쿠키에 저장하여 자동 로그인 지원.
     *
     * @param signInRequestDTO 로그인 요청 데이터 (JSON)
     * @param response HTTP 응답 객체 (쿠키 설정을 위해 필요)
     * @return 로그인 성공 여부, 사용자 정보, 액세스 토큰을 담은 응답 DTO
     */
    @PostMapping("/login")
    public SignInResponseDTO signIn(
            @RequestBody SignInRequestDTO signInRequestDTO,
            HttpServletResponse response
    ) {
        System.out.println(signInRequestDTO); // 요청 데이터 로그 출력

        // 사용자 인증 (ID와 비밀번호 검증)
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequestDTO.getUsername(),
                        signInRequestDTO.getPassword()
                )
        );

        // 인증된 사용자 정보를 Spring Security 컨텍스트에 저장
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        // 인증된 사용자 정보 가져오기
        Member member = ((CustomUserDetails) authenticate.getPrincipal()).getMember();

        // 액세스 토큰 및 리프레시 토큰 발급
        String accessToken = tokenProvider.generateToken(member, Duration.ofHours(2)); // 액세스 토큰 (2시간 유효)
        String refreshToken = tokenProvider.generateToken(member, Duration.ofDays(2)); // 리프레시 토큰 (2일 유효)

        // Refresh Token을 쿠키에 저장 (7일 유지)
        CookieUtil.addCookie(response, "refreshToken", refreshToken, 7 * 24 * 60 * 60);

        // 로그인 성공 응답 반환
        return SignInResponseDTO.builder()
                .isLoggined(true) // 로그인 성공 여부
                .token(accessToken) // JWT 액세스 토큰
                .userId(member.getUserId()) // 사용자 ID
                .userName(member.getUserName()) // 사용자 이름
                .build();
    }
    /**
     * ✅ 로그아웃 요청을 처리하는 API.
     * - 저장된 Refresh Token을 삭제하여 로그아웃 처리.
     *
     * @param request HTTP 요청 객체 (쿠키 정보 접근을 위해 필요)
     * @param response HTTP 응답 객체 (쿠키 삭제를 위해 필요)
     */
    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // Refresh Token 삭제
        CookieUtil.deleteCookie(request, response, "refreshToken");
    }

    /**
     * ✅ 현재 로그인된 사용자 정보를 반환하는 API.
     * - 요청 헤더의 JWT 토큰을 이용하여 사용자 정보를 조회.
     *
     * @param request HTTP 요청 객체 (토큰 정보 접근을 위해 필요)
     * @return 사용자 정보 (ID, 이름, 역할 포함)
     */
    @GetMapping("/user/info")
    public UserInfoResponseDTO getUserInfo(HttpServletRequest request) {
        // 요청 객체에서 사용자 정보(Member)를 가져옴
        Member member = (Member) request.getAttribute("member");

        // 사용자 정보를 응답 DTO로 변환하여 반환
        return UserInfoResponseDTO.builder()
                .id(member.getId())
                .userName(member.getUserName())
                .userId(member.getUserId())
                .role(member.getRole())
                .build();
    }
}

