package com.example.basicboard2.model;

import com.example.basicboard2.type.Role;
import lombok.Builder;
import lombok.Getter;

/**
 * ✅ 사용자(Member) 정보를 저장하는 모델 클래스.
 * - 회원 가입 및 로그인 시 사용됨.
 */
@Getter // Lombok: 필드 값을 읽을 수 있도록 Getter 메서드 자동 생성
@Builder // Lombok: 빌더 패턴을 적용하여 객체 생성 지원
public class Member {
    private long id; // 회원 고유 ID (Primary Key)
    private String userId; // 로그인 ID
    private String password; // 비밀번호 (암호화 저장됨)
    private String userName; // 사용자 이름
    private Role role; // 사용자 역할 (예: ROLE_USER, ROLE_ADMIN)
}
