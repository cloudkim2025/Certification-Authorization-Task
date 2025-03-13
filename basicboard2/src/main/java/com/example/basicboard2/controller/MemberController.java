package com.example.basicboard2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 회원 관련 페이지를 처리하는 컨트롤러.
 * - 회원 가입 및 로그인 페이지를 반환하는 역할.
 */
@Controller
@RequestMapping("/member") // "/member" 경로를 기본 매핑
public class MemberController {

    /**
     * 회원 가입 페이지(sign-up.html)로 이동하는 메서드.
     * @return "sign-up" (회원 가입 페이지 뷰)
     */
    @GetMapping("/join")
    public String join(){
        return "sign-up"; // sign-up.html 템플릿 반환
    }

    /**
     * 로그인 페이지(sign-in.html)로 이동하는 메서드.
     * @return "sign-in" (로그인 페이지 뷰)
     */
    @GetMapping("/login")
    public String login(){
        return "sign-in"; // sign-in.html 템플릿 반환
    }
}
