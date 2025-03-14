package com.example.basicboard2.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SocialLoginResponseDTO {
    private String userId;
    private String email;
    private String nickname;
    private String token;

}
