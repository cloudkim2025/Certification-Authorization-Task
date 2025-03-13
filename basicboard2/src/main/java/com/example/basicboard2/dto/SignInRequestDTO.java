package com.example.basicboard2.dto;

import lombok.Getter;
import lombok.ToString;

/**
 * 로그인 요청을 처리하는 DTO (Data Transfer Object).
 * - 클라이언트에서 로그인 요청 시 필요한 데이터를 전달받음.
 */
@Getter
@ToString
public class SignInRequestDTO {
    private String username; // 사용자의 로그인 ID
    private String password; // 사용자의 비밀번호
}
