package com.example.basicboard2.config.jwt;

import com.example.basicboard2.model.Member;
import com.example.basicboard2.type.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j // 로그를 위한 Lombok 어노테이션
@Service // Spring의 Service 계층을 나타내는 어노테이션
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동 생성하는 Lombok 어노테이션
public class TokenProvider {

    private final JwtProperties jwtProperties; // JWT 설정 정보를 담고 있는 객체 (비밀 키, 발급자 정보 등)

    /**
     * ✅ JWT 액세스 토큰을 생성하는 메서드
     * @param member - 로그인한 사용자 정보
     * @param expiredAt - 토큰 만료 시간 (예: 2시간)
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(Member member, Duration expiredAt) {
        Date now = new Date(); // 현재 시간
        return makeToken(
                member,
                new Date(now.getTime() + expiredAt.toMillis()) // 만료 시간 설정 (현재 시간 + 지정된 만료 시간)
        );
    }

    /**
     * ✅ JWT 토큰에서 사용자 정보를 추출하는 메서드
     * - 클레임(Claims)에서 사용자 ID, 이름, 역할(Role) 정보를 가져와 Member 객체로 변환
     * @param token - JWT 토큰
     * @return Member 객체 (토큰에 저장된 사용자 정보)
     */
    public Member getTokenDetails(String token) {
        Claims claims = getClaims(token); // 토큰에서 클레임 정보 추출

        return Member.builder()
                .id(claims.get("id", Long.class)) // 사용자 ID
                .userId(claims.getSubject()) // 사용자 아이디 (subject)
                .userName(claims.get("userName", String.class)) // 사용자 이름
                .role(Role.valueOf(claims.get("role", String.class))) // 역할(Role)
                .build();
    }

    /**
     * ✅ JWT 토큰에서 인증(Authentication) 객체를 생성하는 메서드
     * - Spring Security에서 사용자의 권한을 인증하는 데 사용됨
     * @param token - JWT 토큰
     * @return Authentication 객체 (SecurityContext에서 인증에 사용)
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token); // JWT에서 클레임(Claims) 추출

        // Claims에서 역할을 추출하고, GrantedAuthority로 변환
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(claims.get("role", String.class))
        );

        // UserDetails 객체 생성 (Spring Security에서 사용)
        UserDetails userDetails = new User(claims.getSubject(), "", authorities);

        // UsernamePasswordAuthenticationToken 생성하여 반환
        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    /**
     * ✅ JWT 토큰이 유효한지 검증하는 메서드
     * @param token - 클라이언트가 보낸 JWT 토큰
     * @return 1 (유효한 토큰), 2 (토큰 만료됨), 3 (잘못된 토큰)
     */
    public int validToken(String token) {
        try {
            getClaims(token); // 토큰 서명 검증
            return 1; // 유효한 토큰
        } catch (ExpiredJwtException e) {
            log.info("Token이 만료되었습니다."); // 토큰이 만료됨
            return 2;
        } catch (Exception e) {
            log.error("Token 복호화 에러: {}", e.getMessage()); // 복호화 실패 (유효하지 않은 토큰)
            return 3;
        }
    }

    /**
     * ✅ JWT 토큰을 실제로 생성하는 메서드
     * @param member - JWT에 저장할 사용자 정보
     * @param expired - 토큰 만료 시간
     * @return JWT 토큰 문자열
     */
    private String makeToken(Member member, Date expired) {
        Date now = new Date(); // 현재 시간

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // JWT 헤더 설정
                .setIssuer(jwtProperties.getIssuer()) // 토큰 발급자(issuer) 설정
                .setIssuedAt(now) // 토큰 발급 시간
                .setExpiration(expired) // 만료 시간 설정
                .setSubject(member.getUserId()) // 사용자 ID 저장 (subject)
                .claim("id", member.getId()) // 사용자 ID
                .claim("role", member.getRole().name()) // 역할(Role)
                .claim("userName", member.getUserName()) // 사용자 이름
                .signWith(getSecretKey(), SignatureAlgorithm.HS512) // 서명(Signature) 생성 (비밀 키 사용)
                .compact(); // 토큰 생성 완료
    }

    /**
     * ✅ JWT 서명을 검증하고 클레임(Claims) 정보를 가져오는 메서드
     * @param token - JWT 토큰
     * @return 토큰에서 추출한 클레임 정보
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey()) // 서명 키 검증
                .build()
                .parseClaimsJws(token) // 토큰 검증
                .getBody(); // 클레임 반환
    }

    /**
     * ✅ 비밀 키를 가져오는 메서드 (JWT 서명에 사용)
     * - HMAC SHA512 알고리즘을 사용하여 서명
     * @return SecretKey (JWT 서명에 사용되는 비밀 키)
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
