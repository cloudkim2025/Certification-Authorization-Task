package com.example.basicboard2.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ✅ 게시판 관련 화면을 처리하는 컨트롤러.
 * - HTML 페이지를 반환하는 역할.
 */
@Controller // Spring MVC의 컨트롤러로 등록 (View를 반환)
public class BoardController {

    /**
     * ✅ 게시글 목록 페이지를 반환하는 메서드.
     * - `/` 요청 시 `board-list.html`을 반환.
     * @return 게시글 목록 페이지 (Thymeleaf 템플릿)
     */
    @GetMapping("/")
    public String boardList() {
        return "board-list"; // `resources/templates/board-list.html` 반환
    }

    /**
     * ✅ 게시글 작성 페이지를 반환하는 메서드.
     * - `/write` 요청 시 `board-write.html`을 반환.
     * @return 게시글 작성 페이지 (Thymeleaf 템플릿)
     */
    @GetMapping("/write")
    public String boardWrite() {
        return "board-write"; // `resources/templates/board-write.html` 반환
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long id, Model model) {
        model.addAttribute("id", id);
        return "board-detail";
    }
    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return "board-update";
    }
}
