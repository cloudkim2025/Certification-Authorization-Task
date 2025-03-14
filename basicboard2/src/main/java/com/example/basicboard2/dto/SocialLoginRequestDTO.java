package com.example.basicboard2.dto;

import com.example.basicboard2.type.SocialLoginType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialLoginRequestDTO {
    private SocialLoginType socialLoginType;
    private String accessToken;
}
