package com.example.basicboard2.service;

import com.example.basicboard2.config.jwt.TokenProvider;
import com.example.basicboard2.dto.SignInResponseDTO;
import com.example.basicboard2.model.Member;
import com.example.basicboard2.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenApiService {

    private final TokenProvider tokenProvider;

    public SignInResponseDTO refreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken != null && tokenProvider.validToken(refreshToken) == 1) {
            Member member = tokenProvider.getTokenDetails(refreshToken);

            String newAccessToken = tokenProvider.generateToken(member, Duration.ofHours(2));
            String newRefreshToken = tokenProvider.generateToken(member, Duration.ofDays(2));

            CookieUtil.addCookie(response, "refreshToken", newRefreshToken, 7 * 24 * 60 * 60);
            response.setHeader(HttpHeaders.AUTHORIZATION, newAccessToken);

            return SignInResponseDTO.builder()
                    .token(newAccessToken)
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token이 유효하지 않습니다.");
        }
    }
}
