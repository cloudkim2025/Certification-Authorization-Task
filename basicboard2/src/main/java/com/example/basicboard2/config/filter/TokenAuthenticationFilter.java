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
@Component
@RequiredArgsConstructor
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

        String requestURI = request.getRequestURI();
        log.info("Request URI: {}", requestURI);

        // ✅ "/refresh-token" 요청은 필터를 적용하지 않음
        if ("/refresh-token".equals(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 1️⃣ 요청 헤더에서 JWT 토큰 추출
        String token = resolveToken(request);

        if (token == null || token.isEmpty()) {
            log.warn("Authorization 헤더가 없음 또는 비어 있음");
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 2️⃣ 토큰이 JWT 형식인지 확인
        if (!token.contains(".")) {
            log.error("유효하지 않은 JWT 형식: {}", token);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT format");
            return;
        }

        // ✅ 3️⃣ 토큰이 유효한 경우, SecurityContext에 인증 정보 저장
        int tokenValidation = tokenProvider.validToken(token);

        if (tokenValidation == 1) { // 정상 토큰
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // ✅ 4️⃣ 토큰에서 사용자 정보를 가져와 request 객체에 저장
            Member member = tokenProvider.getTokenDetails(token);
            request.setAttribute("member", member);
        }
        else if (tokenValidation == 2) { // 만료된 토큰
            log.warn("JWT 토큰 만료됨");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT token");
            return;
        }
        else { // 유효하지 않은 토큰
            log.error("JWT 검증 실패");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        // ✅ 5️⃣ 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }

    /**
     * ✅ HTTP 요청 헤더에서 JWT 토큰을 추출하는 메서드
     * @param request - HTTP 요청 객체
     * @return JWT 토큰 (Bearer 제거 후 반환) 또는 null
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);

        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length()); // "Bearer " 이후의 값만 추출
        }

        return null; // 토큰이 없으면 null 반환
    }
}
