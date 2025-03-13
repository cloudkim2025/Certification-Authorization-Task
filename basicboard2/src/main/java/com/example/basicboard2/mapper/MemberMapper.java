package com.example.basicboard2.mapper;

import com.example.basicboard2.model.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis를 사용하여 데이터베이스와 연결하는 Mapper 인터페이스.
 * - 회원(Member) 정보를 데이터베이스에 저장하거나 조회하는 기능을 제공.
 */
@Mapper // MyBatis의 Mapper 인터페이스로 등록
public interface MemberMapper {

    /**
     * 회원 정보를 데이터베이스에 저장하는 메서드.
     * @param member 저장할 회원 정보 객체
     */
    void saved(Member member);

    /**
     * 주어진 사용자 ID(userId)로 회원 정보를 조회하는 메서드.
     * @param userId 조회할 사용자의 로그인 ID
     * @return 조회된 회원 정보(Member 객체)
     */
    Member findByUserId(String userId);
}
