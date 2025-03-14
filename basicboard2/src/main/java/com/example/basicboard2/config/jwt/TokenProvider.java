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

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;

    /**
     *  JWT 액세스 토큰을 생성하는 메서드
     */
    public String generateToken(Member member, Duration expiredAt) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiredAt.toMillis());

        log.info("generateToken() - JWT 생성 시작, 사용자 ID: {}", member.getUserId());

        // JWT 생성
        String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 설정
                .setIssuer(jwtProperties.getIssuer()) // 발급자 설정
                .setIssuedAt(now) // 발급 시간
                .setExpiration(expiryDate) // 만료 시간 설정
                .setSubject(member.getUserId()) // 사용자 ID 저장
                .claim("id", member.getId()) // 사용자 ID
                .claim("role", member.getRole().name()) // 역할(Role)
                .claim("userName", member.getUserName()) // 사용자 이름
                .signWith(getSecretKey(), SignatureAlgorithm.HS512) // 서명(Signature) 생성 (비밀 키 사용)
                .compact(); // 토큰 생성 완료

        log.info("generateToken() - 생성된 JWT: {}", token);
        return token;
    }

    /**
     * JWT 토큰에서 사용자 정보를 추출하는 메서드
     */
    public Member getTokenDetails(String token) {
        Claims claims = getClaims(token);

        return Member.builder()
                .id(claims.get("id", Long.class))
                .userId(claims.getSubject())
                .userName(claims.get("userName", String.class))
                .role(Role.valueOf(claims.get("role", String.class)))
                .build();
    }

    /**
     * ✅ JWT 토큰에서 인증(Authentication) 객체를 생성하는 메서드
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(claims.get("role", String.class))
        );

        UserDetails userDetails = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    /**
     * ✅ JWT 토큰이 유효한지 검증하는 메서드
     */
    public int validToken(String token) {
        try {
            getClaims(token);
            return 1; // 유효한 토큰
        } catch (ExpiredJwtException e) {
            log.info("Token이 만료되었습니다.");
            return 2;
        } catch (Exception e) {
            log.error("Token 복호화 에러: {}", e.getMessage());
            return 3;
        }
    }

    /**
     * ✅ JWT 서명을 검증하고 클레임(Claims) 정보를 가져오는 메서드
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * ✅ 비밀 키를 가져오는 메서드
     */
    private SecretKey getSecretKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecretKey());
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("SecretKey 생성 오류: {}", e.getMessage());
            throw new RuntimeException("비밀 키를 생성할 수 없습니다.");
        }
    }
}
