package com.example.basicboard2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverUserResponse {
    private NaverResponse response; // 이 필드가 반드시 있어야 합니다.

    @Override
    public String toString() {
        return "NaverUserResponse{" +
                "response=" + response +
                '}';
    }

    @Getter
    @Setter
    public static class NaverResponse {
        private String id;
        private String email;
        private String nickname;

        @Override
        public String toString() {
            return "NaverResponse{" +
                    "id='" + id + '\'' +
                    ", email='" + email + '\'' +
                    ", nickname='" + nickname + '\'' +
                    '}';
        }

    }
}
