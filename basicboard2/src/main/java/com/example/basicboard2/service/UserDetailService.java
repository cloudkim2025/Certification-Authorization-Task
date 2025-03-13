package com.example.basicboard2.service;

import com.example.basicboard2.config.security.CustomUserDetails;
import com.example.basicboard2.mapper.MemberMapper;
import com.example.basicboard2.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security에서 사용자 인증을 처리하는 서비스 클래스.
 * - 사용자의 로그인 요청이 들어오면 DB에서 사용자 정보를 조회하여 인증을 수행.
 */
@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final MemberMapper memberMapper; // MyBatis를 통해 DB에서 회원 정보를 조회하는 Mapper

    /**
     * 주어진 사용자 이름(로그인 ID)으로 회원 정보를 조회하여 Spring Security의 UserDetails 객체로 변환.
     * @param username 사용자의 로그인 ID
     * @return UserDetails 객체 (Spring Security에서 사용)
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 예외 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 데이터베이스에서 사용자 정보 조회
        Member member = memberMapper.findByUserId(username);

        // 사용자가 존재하지 않으면 예외 발생
        if (member == null) {
            throw new UsernameNotFoundException(username + " not found");
        }

        // 조회된 사용자를 CustomUserDetails 객체로 변환하여 반환
        return CustomUserDetails.builder()
                .member(member) // 사용자 정보 설정
                .roles(List.of(String.valueOf(member.getRole().name()))) // 역할(Role) 정보 설정
                .build();
    }
}
