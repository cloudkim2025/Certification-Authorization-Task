package com.example.basicboard2.controller;

import com.example.basicboard2.dto.SignInResponseDTO;
import com.example.basicboard2.service.TokenApiService;
import com.example.basicboard2.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class TokenApiController {

        private final TokenApiService tokenApiService;

    @PostMapping("/refresh-token")
    public ResponseEntity<SignInResponseDTO> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookieValue(request, "refreshToken");
        SignInResponseDTO responseDTO = tokenApiService.refreshToken(refreshToken, response);
        return ResponseEntity.ok(responseDTO);
    }
}
