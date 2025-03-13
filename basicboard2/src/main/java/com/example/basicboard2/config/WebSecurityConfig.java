package com.example.basicboard2.config;

import com.example.basicboard2.config.filter.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * ✅ Spring Security 설정 클래스 (WebSecurityConfig)
 * - JWT 기반 인증을 사용하도록 설정
 * - 특정 API 엔드포인트는 인증 없이 접근 가능
 * - 세션을 사용하지 않고 STATELESS 방식 적용
 */
@Configuration // Spring Security 설정 클래스임을 명시
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성 (Lombok)
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter; // JWT 인증 필터

    /**
     * ✅ 정적 리소스 (CSS, JS) 요청을 보안 필터에서 제외
     * - 보안 검사를 하지 않도록 설정
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/static/**",
                        "/css/**",
                        "/js/**"
                ); // 정적 리소스 요청은 필터링 제외
    }

    /**
     * ✅ Spring Security의 보안 정책을 설정하는 메서드
     * - JWT 인증 필터를 추가하여 모든 요청에 대해 인증 적용
     * - 특정 경로는 인증 없이 접근 가능하도록 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF(Cross-Site Request Forgery) 보호 기능 비활성화
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ) // 세션을 사용하지 않고, STATELESS 방식 적용
                .authorizeHttpRequests(auth -> auth
                               .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers(
                                        new AntPathRequestMatcher("/", GET.name()), // 메인 페이지
                                        new AntPathRequestMatcher("/member/join", GET.name()), // 회원가입 페이지
                                        new AntPathRequestMatcher("/member/login", GET.name()), // 로그인 페이지
                                        new AntPathRequestMatcher("/write", GET.name()), // 글쓰기 페이지
                                        new AntPathRequestMatcher("/detail", GET.name()),
                                        new AntPathRequestMatcher("/update/*", GET.name()),
                                        new AntPathRequestMatcher("/access-denied", GET.name()),

                                        new AntPathRequestMatcher("/refresh-token", POST.name()),
                                        new AntPathRequestMatcher("/join", POST.name()), // 회원가입 요청
                                        new AntPathRequestMatcher("/login", POST.name()), // 로그인 요청
                                        new AntPathRequestMatcher("/logout", POST.name()), // 로그아웃 요청
                                        new AntPathRequestMatcher("/logout", POST.name()),
                                        new AntPathRequestMatcher("/api/board/file/download/*", GET.name())
                                ).permitAll() // 위의 경로들은 인증 없이 접근 가능
                                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .logout(AbstractHttpConfigurer::disable) // 로그아웃 기능 비활성화 (JWT 기반 인증에서는 필요 없음)
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
        )
        ;
        return http.build();
    }

    /**
     * ✅ AuthenticationManager 빈 생성
     * - Spring Security의 인증을 관리하는 객체
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * ✅ 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 생성
     * - 회원가입 시 비밀번호를 암호화하여 저장
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.sendRedirect("/access-denied");
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, accessDeniedException) -> {
            response.sendRedirect("/access-denied");
        };
    }
}
