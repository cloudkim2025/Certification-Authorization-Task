package com.example.basicboard2.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 쿠키(Cookie) 관련 유틸리티 클래스.
 * - 쿠키를 추가하거나 삭제하는 기능을 제공.
 */
public class CookieUtil {

    /**
     * 쿠키를 추가하는 메서드.
     * - HTTPOnly, Secure 설정을 적용하여 보안 강화.
     *
     * @param response 응답 객체 (HttpServletResponse)
     * @param name     쿠키 이름
     * @param value    쿠키 값
     * @param maxAge   쿠키 유지 시간 (초 단위)
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); // JavaScript에서 접근할 수 없도록 설정 (XSS 공격 방지)
        cookie.setSecure(true);   // HTTPS 환경에서만 전송되도록 설정 (보안 강화)
        cookie.setPath("/"); // 애플리케이션의 모든 경로에서 쿠키 사용 가능
        cookie.setMaxAge(maxAge); // 쿠키의 만료 시간 설정 (초 단위)
        response.addCookie(cookie); // HTTP 응답에 쿠키 추가
    }

    /**
     * 쿠키를 삭제하는 메서드.
     * - 동일한 이름을 가진 쿠키를 찾아 만료시킴.
     *
     * @param request  요청 객체 (HttpServletRequest)
     * @param response 응답 객체 (HttpServletResponse)
     * @param name     삭제할 쿠키의 이름
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return; // 쿠키가 없으면 삭제할 필요 없음
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) { // 해당 쿠키가 존재하면 삭제 처리
                cookie.setMaxAge(0); // 즉시 만료
                cookie.setValue(""); // 빈 값으로 설정
                cookie.setPath("/"); // 동일한 경로 설정 (삭제 적용)
                response.addCookie(cookie); // 변경된 쿠키를 응답에 추가
            }
        }
    }

    /**
     * HTTP 요청에서 특정 이름을 가진 쿠키의 값을 가져오는 메서드.
     *
     * @param request HTTP 요청 객체
     * @param name    가져오려는 쿠키의 이름
     * @return 해당 쿠키의 값 (쿠키가 존재하지 않으면 null 반환)
     */
    public static String getCookieValue(HttpServletRequest request, String name) {

        // 요청에서 쿠키 배열이 존재하는지 확인 (쿠키가 없는 경우 null이 반환될 수 있음)
        if (request.getCookies() != null) {

            // 요청에서 가져온 모든 쿠키를 반복하여 검사
            for (Cookie cookie : request.getCookies()) {

                // 현재 쿠키의 이름이 찾고자 하는 이름(name)과 동일한 경우
                if (name.equals(cookie.getName())) {

                    // 해당 쿠키의 값을 반환
                    return cookie.getValue();
                }
            }
        }

        // 요청된 쿠키가 존재하지 않거나 찾을 수 없는 경우 null 반환
        return null;
    }
}

