package com.example.basicboard2.config.security;

import com.example.basicboard2.model.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security에서 사용자 정보를 관리하는 CustomUserDetails 클래스
 * Spring Security의 UserDetails 인터페이스를 구현하여 사용자 인증 및 권한 관리 수행
 */
@Getter
@Builder
public class CustomUserDetails implements UserDetails {

    private Member member; // 사용자의 정보를 담는 Member 객체
    private List<String> roles; // 사용자의 역할(Role) 목록

    /**
     * 사용자의 권한 목록을 반환하는 메서드
     * Spring Security에서 사용자의 권한을 관리하기 위해 GrantedAuthority 객체로 변환
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new) // 역할(Role) 이름을 SimpleGrantedAuthority로 변환
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 비밀번호를 반환하는 메서드
     * Spring Security에서 비밀번호 검증 시 사용됨
     */
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    /**
     * 사용자의 고유 식별자(ID)를 반환하는 메서드
     * 로그인 시 사용하는 사용자 이름(username)
     */
    @Override
    public String getUsername() {
        return member.getUserId();
    }

    /**
     * 계정이 만료되지 않았는지 여부를 반환
     * true일 경우 계정이 활성 상태로 간주됨
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정이 잠겨 있지 않은지 여부를 반환
     * true일 경우 계정이 잠기지 않은 상태로 간주됨
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 사용자의 인증 정보(비밀번호)가 만료되지 않았는지 여부를 반환
     * true일 경우 인증 정보가 유효함
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정이 활성화(사용 가능) 상태인지 여부를 반환
     * true일 경우 계정이 활성화된 상태로 간주됨
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
