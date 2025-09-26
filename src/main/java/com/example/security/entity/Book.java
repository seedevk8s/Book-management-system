package com.example.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 책 정보를 저장하는 엔티티
 *
 * 추가 기능:
 * - 책을 등록한 사용자 정보 저장 (작성자)
 * - 등록 시간 자동 기록
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book")  // 테이블명 명시적 지정
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 책 고유 식별자

    @Column(nullable = false, length = 200)
    private String title;           // 책 제목

    @Column(nullable = false)
    private Integer price;          // 가격 (null 방지를 위해 Integer 사용)

    @Column(nullable = false, length = 100)
    private String author;          // 저자명

    @Column(nullable = false)
    private Integer page;           // 페이지 수

    @Column(length = 500)
    private String description;     // 책 설명 (선택사항)

    // 책을 등록한 회원과의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)  // 지연 로딩으로 성능 최적화
    @JoinColumn(name = "member_id")     // FK 컬럼명
    private Member registeredBy;     // 등록한 회원

    /*
    양방향은 다음 경우에만 고려:
    Member 객체에서 자주 Book 목록에 접근해야 할 때
    캐스케이드 작업이 필요할 때
    비즈니스 로직상 Member가 Book을 관리해야 할 때

    단방향만 사용해도 되는 이유:
    1. 비즈니스 요구사항 충족
    "이 책을 누가 등록했는가?" → book.getRegisteredBy() ✅
    "이 회원이 등록한 모든 책은?" → Repository에서 쿼리로 해결 가능
    2. 장점
    ✅ 단순한 구조: 관리 포인트가 하나
    ✅ 순환 참조 없음: JSON 직렬화 시 문제 없음
    ✅ 성능 이슈 없음: 필요한 방향으로만 조회
    ✅ 유지보수 용이: 양방향 동기화 걱정 없음
     */

    // 등록 시간 자동 설정
    @Column(nullable = false, updatable = false)  // 수정 불가
    private LocalDateTime createdAt;

    // 수정 시간 자동 설정
    private LocalDateTime updatedAt;

    // 엔티티 저장 전 자동 실행
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // 엔티티 수정 전 자동 실행
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}
