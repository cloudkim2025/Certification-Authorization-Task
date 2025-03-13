package com.example.basicboard2.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 응답을 처리하는 DTO (Data Transfer Object).
 * - 클라이언트에게 로그인 결과를 전달하는 역할.
 */
@Getter
@Builder
public class SignInResponseDTO {
    private boolean isLoggined; // 로그인 성공 여부 (true: 로그인 성공, false: 로그인 실패)
    private String userId; // 사용자의 로그인 ID
    private String userName; // 사용자의 이름
    private String token; // 로그인 성공 시 발급된 JWT 토큰
}
