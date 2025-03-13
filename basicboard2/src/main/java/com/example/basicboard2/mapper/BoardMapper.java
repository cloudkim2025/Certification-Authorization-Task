package com.example.basicboard2.mapper;

import com.example.basicboard2.model.Article;
import com.example.basicboard2.model.Paging;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ✅ 게시판 관련 SQL 실행을 위한 MyBatis Mapper 인터페이스.
 * - 데이터베이스와 직접 연결되어 SQL을 실행함.
 */
@Mapper // MyBatis 매퍼 인터페이스로 등록
public interface BoardMapper {

    /**
     * ✅ 게시글을 데이터베이스에 저장하는 메서드.
     * @param article 저장할 게시글 객체
     */
    void saveArticle(Article article);

    /**
     * ✅ 게시글 목록을 조회하는 메서드 (페이징 적용).
     * @param page 페이징 정보 (offset, size 포함)
     * @return 조회된 게시글 목록
     */
    List<Article> getArticles(Paging page);

    /**
     * ✅ 전체 게시글 개수를 조회하는 메서드.
     * @return 총 게시글 개수
     */
    int getArticleCnt();

    Article getArticleById(long id);
    void updateArticle(Article article);
    void deleteBoardById(long id);
}
