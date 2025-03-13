package com.example.basicboard2.service;

import com.example.basicboard2.mapper.MemberMapper;
import com.example.basicboard2.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ✅ 회원 관련 비즈니스 로직을 처리하는 서비스 클래스.
 * - 회원 가입 등의 기능을 제공.
 */
@Service // Spring의 Service 계층으로 등록
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성 (Lombok)
public class MemberService {

    private final MemberMapper memberMapper; // MyBatis를 사용하여 데이터베이스와 연결하는 Mapper

    /**
     * ✅ 회원 가입을 처리하는 메서드.
     * - 회원 정보를 데이터베이스에 저장.
     * @param member 저장할 회원 정보 객체
     */
    public void signUp(Member member) {
        memberMapper.saved(member); // 데이터베이스에 회원 정보 저장
    }
}
