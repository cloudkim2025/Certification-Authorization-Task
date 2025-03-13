package com.example.basicboard2.config.filter;

import com.example.basicboard2.config.jwt.TokenProvider;
import com.example.basicboard2.model.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ✅ JWT 인증 필터 (TokenAuthenticationFilter)
 * - 모든 요청(Request)에 대해 JWT 토큰을 검사
 * - 유효한 토큰이면 인증(Authentication) 객체를 SecurityContext에 저장
 */
@Slf4j
@Component // Spring의 Bean으로 등록 (자동 관리)
@RequiredArgsConstructor // final 필드를 포함한 생성자를 자동 생성
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider; // JWT 토큰을 관리하는 Provider
    private final static String HEADER_AUTHORIZATION = "Authorization"; // 헤더 이름
    private final static String TOKEN_PREFIX = "Bearer "; // JWT 토큰 접두사 (Bearer 사용)

    /**
     * ✅ 요청이 들어올 때마다 실행되는 필터
     * @param request - HTTP 요청 객체
     * @param response - HTTP 응답 객체
     * @param filterChain - 필터 체인 (다음 필터 호출)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 현재 요청의 URI(경로)를 가져옴
        // 예를 들어, 사용자가 "/api/users"에 접근하면 requestURI 값은 "/api/users"가 됨
        String requestURI = request.getRequestURI();

        // 요청된 URI를 로그에 출력하여 추적 가능하게 함
        log.info("Request URI: {}", requestURI);

        // 요청 URI가 "/refresh-token"인지 확인 (JWT 토큰을 갱신하는 API 엔드포인트)
        if ("/refresh-token".equals(requestURI)) {

            // "/refresh-token" 요청이 들어온 경우, 필터를 적용하지 않고 다음 필터 또는 컨트롤러로 바로 전달
            filterChain.doFilter(request, response);

            // 이후의 필터 로직을 수행하지 않고 메서드 실행 종료
            return;
        }

        // (추가적인 필터링 로직이 여기에 들어갈 수 있음)
        // }


    // 1️⃣ 요청 헤더에서 JWT 토큰 추출
        String token = resolveToken(request);

        // 2️⃣ 토큰이 유효한 경우, SecurityContext에 인증 정보 저장
        if (token != null && tokenProvider.validToken(token) == 1) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3️⃣ 토큰에서 사용자 정보를 가져와 request 객체에 저장
            Member member = tokenProvider.getTokenDetails(token);
            request.setAttribute("member", member);
        }
        // 4️⃣ 토큰이 만료된 경우, HTTP 상태 코드 401 (Unauthorized) 반환
        else if (token != null && tokenProvider.validToken(token) == 2) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return; // 요청을 더 이상 진행하지 않음
        }

        // 5️⃣ 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }

    /**
     * ✅ HTTP 요청 헤더에서 JWT 토큰을 추출하는 메서드
     * @param request - HTTP 요청 객체
     * @return JWT 토큰 (Bearer 제거 후 반환) 또는 null
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);

        // 헤더가 존재하고, "Bearer "로 시작하면 토큰 값만 반환
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length()); // "Bearer " 이후의 값만 추출
        }

        return null; // 토큰이 없으면 null 반환
    }
}
