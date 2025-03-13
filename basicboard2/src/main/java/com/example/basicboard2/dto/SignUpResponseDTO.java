package com.example.basicboard2.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 회원 가입 요청에 대한 응답을 처리하는 DTO (Data Transfer Object).
 * - 클라이언트에게 회원 가입 성공 여부를 전달하는 역할.
 */
@Getter
@Builder
public class SignUpResponseDTO {
    private boolean successed; // 회원 가입 성공 여부 (true: 성공, false: 실패)
}
