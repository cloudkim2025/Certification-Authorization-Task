package com.example.basicboard2.dto;

import com.example.basicboard2.model.Member;
import com.example.basicboard2.type.Role;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 회원 가입 요청을 처리하는 DTO (Data Transfer Object).
 * - 클라이언트에서 회원 가입 요청 시 필요한 데이터를 전달받음.
 * - DTO에서 엔터티(Member)로 변환하는 기능을 포함.
 */
@Getter
@ToString
public class SignUpRequestDTO {
    private String userId; // 사용자의 로그인 ID
    private String password; // 비밀번호 (암호화 필요)
    private String userName; // 사용자 이름
    private Role role; // 사용자 역할 (ROLE_USER, ROLE_ADMIN 등)

    /**
     * DTO를 Member 엔터티 객체로 변환하는 메서드.
     * - 비밀번호를 BCrypt 해싱하여 저장.
     * @param bCryptPasswordEncoder Spring Security의 비밀번호 암호화 객체
     * @return Member 객체 (회원 정보)
     */
    public Member toMember(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Member.builder()
                .userId(userId)
                .password(bCryptPasswordEncoder.encode(password)) // 비밀번호 암호화
                .userName(userName)
                .role(role)
                .build();
    }
}
