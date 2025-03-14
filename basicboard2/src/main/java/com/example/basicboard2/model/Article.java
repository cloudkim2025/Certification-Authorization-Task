package com.example.basicboard2.model;

import com.example.basicboard2.dto.BoardDetailResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ✅ 게시글(Article) 정보를 저장하는 모델 클래스.
 * - 게시판의 각 게시글 데이터를 나타냄.
 */
@Getter
@Builder
public class Article {
    private Long id;            // 게시글 고유 ID (Primary Key)
    private String title;       // 게시글 제목
    private String content;     // 게시글 내용
    private String userId;      // 작성자 ID (Member 테이블과 연관)
    private String nickname;    // 작성자 닉네임 (네이버 로그인 등 소셜 로그인 시 사용)
    private String filePath;    // 첨부 파일 경로
    private LocalDateTime created;  // 생성일
    private LocalDateTime updated;  // 수정일

    public BoardDetailResponseDTO toBoardDetailResponseDTO() {
        return BoardDetailResponseDTO.builder()
                .title(title)
                .content(content)
                .userId(userId)
                .nickname(nickname)
                .filePath(filePath)
                .created(created)
                .build();
    }
}
