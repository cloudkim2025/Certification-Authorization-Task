package com.example.basicboard2.dto;

import com.example.basicboard2.model.Article;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * ✅ 게시판 목록 조회 응답을 위한 DTO (Data Transfer Object).
 * - 클라이언트에게 게시글 목록을 반환할 때 사용됨.
 */
@Getter // Lombok: 필드 값을 읽을 수 있도록 Getter 메서드 자동 생성
@Builder // Lombok: 빌더 패턴을 적용하여 객체 생성 지원
public class BoardListResponseDTO {
    List<Article> articles; // 게시글 목록
    boolean last; // 마지막 페이지 여부
}
