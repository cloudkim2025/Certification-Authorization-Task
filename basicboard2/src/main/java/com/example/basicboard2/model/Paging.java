package com.example.basicboard2.model;

import lombok.Builder;
import lombok.Getter;

/**
 * ✅ 페이징 처리를 위한 모델 클래스.
 * - 게시글 목록을 가져올 때, 페이지 번호와 크기를 설정하는 용도로 사용됨.
 */
@Getter // Lombok: 필드 값을 읽을 수 있도록 Getter 메서드 자동 생성
@Builder // Lombok: 빌더 패턴을 적용하여 객체 생성 지원
public class Paging {
    private int offset; // 조회할 데이터의 시작 위치 (페이징 계산에 사용됨)
    private int size; // 한 번에 가져올 데이터 개수
}
