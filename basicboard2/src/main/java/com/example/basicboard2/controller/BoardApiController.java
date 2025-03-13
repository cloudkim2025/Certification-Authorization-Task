package com.example.basicboard2.controller;

import com.example.basicboard2.dto.BoardDeleteRequestDTO;
import com.example.basicboard2.dto.BoardDetailResponseDTO;
import com.example.basicboard2.dto.BoardListResponseDTO;
import com.example.basicboard2.model.Article;
import com.example.basicboard2.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * ✅ 게시판 관련 API 요청을 처리하는 컨트롤러.
 * - JSON 데이터를 반환하는 REST API.
 */
@RestController // RESTful API 컨트롤러
@RequiredArgsConstructor // Lombok: 생성자 자동 주입
@RequestMapping("/api/board") // API 기본 경로 설정
public class BoardApiController {

    private final BoardService boardService; // 게시글 관련 서비스

    /**
     * ✅ 게시글 목록 조회 API (페이징 포함).
     * @param page 요청한 페이지 번호 (기본값: 1)
     * @param size 페이지당 게시글 개수 (기본값: 10)
     * @return 게시글 목록과 마지막 페이지 여부
     */
    //    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public BoardListResponseDTO getBoards(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        // 게시글 목록 가져오기
        List<Article> articles = boardService.getBoardArticles(page, size);
        // 전체 게시글 수 가져오기
        int totalArticleCnt = boardService.getTotalArticleCnt();

        // 마지막 페이지 여부 계산
        boolean last = (page * size) >= totalArticleCnt;

        return BoardListResponseDTO.builder()
                .articles(articles) // 게시글 목록 설정
                .last(last) // 마지막 페이지 여부 설정
                .build();
    }

    @GetMapping("/{id}")
    public BoardDetailResponseDTO getBoardDetail(@PathVariable long id) {
        return boardService
                .getBoardDetail(id)
                .toBoardDetailResponseDTO();
    }

    /**
     * ✅ 게시글 저장 API.
     * - 파일 업로드 포함.
     * @param title 게시글 제목
     * @param content 게시글 내용
     * @param userId 작성자 ID
     * @param file 첨부 파일 (선택 사항)
     */
    @PostMapping
    public void saveArticle(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("hiddenUserId") String userId,
            @RequestParam("file") MultipartFile file
    ) {
        boardService.saveArticle(userId, title, content, file);
    }

    @PutMapping
    public void updateArticle(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("hiddenUserId") String userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("hiddenId") Long id,
            @RequestParam("hiddenFileFlag") Boolean fileChanged,
            @RequestParam("hiddenFilePath") String filePath
    ) {
    }


    @GetMapping("/file/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = boardService.downloadFile(fileName);

        // 한글 파일명을 URL 인코딩
        String encoded = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .body(resource);
    }
    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable long id, @RequestBody BoardDeleteRequestDTO requestDTO) {
        boardService.deleteBoardById(id, requestDTO);
    }
}
