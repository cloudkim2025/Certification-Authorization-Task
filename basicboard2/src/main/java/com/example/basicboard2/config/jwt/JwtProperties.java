package com.example.basicboard2.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 관련 설정 값을 관리하는 클래스.
 * application.yml 또는 application.properties에서 "jwt"로 시작하는 설정 값을 자동으로 바인딩함.
 */
@Getter
@Setter
@Component
@ConfigurationProperties("jwt") // "jwt" prefix를 가진 설정 값을 바인딩
public class JwtProperties {
    private String issuer;   // JWT 발급자(issuer) 설정 값
    private String secretKey; // JWT 서명(Signature)에 사용할 비밀 키
}
