package com.example.basicboard2.model;

import com.example.basicboard2.dto.BoardDetailResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ✅ 게시글(Article) 정보를 저장하는 모델 클래스.
 * - 게시판의 각 게시글 데이터를 나타냄.
 */
@Getter // Lombok: 필드 값을 읽을 수 있도록 Getter 메서드 자동 생성
@Builder // Lombok: 빌더 패턴을 적용하여 객체 생성 지원
public class Article {
    private Long id; // 게시글 고유 ID (Primary Key)
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String userId; // 작성자 ID (Member 테이블과 연관)
    private String filePath; // 첨부 파일 경로
    private LocalDateTime created; // 생성일
    private LocalDateTime updated; // 수정일


    public BoardDetailResponseDTO toBoardDetailResponseDTO() {
        return BoardDetailResponseDTO.builder()
                .title(title)
                .content(content)
                .userId(userId)
                .filePath(filePath)
                .created(created)
                .build();
    }
}
