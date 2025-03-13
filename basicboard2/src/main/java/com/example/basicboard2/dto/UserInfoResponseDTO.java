package com.example.basicboard2.dto;

import com.example.basicboard2.type.Role;
import lombok.Builder;
import lombok.Getter;

/**
 * ✅ 사용자 정보 조회 응답을 위한 DTO (Data Transfer Object).
 * - 클라이언트에게 사용자 정보를 반환할 때 사용됨.
 */
@Getter // Lombok: 필드 값을 읽을 수 있도록 Getter 메서드 자동 생성
@Builder // Lombok: 빌더 패턴을 적용하여 객체 생성 지원
public class UserInfoResponseDTO {
    private Long id; // 사용자 고유 ID
    private String userName; // 사용자 이름
    private String userId; // 사용자 로그인 ID
    private Role role; // 사용자 역할 (예: ROLE_USER, ROLE_ADMIN)
}
