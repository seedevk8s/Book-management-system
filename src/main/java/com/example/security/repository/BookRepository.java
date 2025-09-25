package com.example.security.repository;

import com.example.security.entity.Book;
import com.example.security.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 책 데이터 접근을 위한 Repository
 *
 * JpaRepository를 상속받아 기본 CRUD 메서드 자동 제공:
 * - save(), findById(), findAll(), delete() 등
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // 제목으로 책 검색 (부분 일치, 대소문자 구분 없음)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // 저자명으로 책 검색
    List<Book> findByAuthorContaining(String author);

    // 특정 회원이 등록한 책 목록 조회
    List<Book> findByRegisteredBy(Member member);

    // 가격 범위로 책 검색
    List<Book> findByPriceBetween(Integer minPrice, Integer maxPrice);

    // 최신 등록순으로 책 목록 조회 (JPQL 사용)
    @Query("SELECT b FROM Book b ORDER BY b.createdAt DESC")
    List<Book> findAllOrderByCreatedAtDesc();

    // 특정 회원이 등록한 책 개수 조회
    @Query("SELECT COUNT(b) FROM Book b WHERE b.registeredBy.id = :memberId")
    Long countByMemberId(@Param("memberId") Long memberId);

    // 책과 등록자 정보를 함께 조회 (N+1 문제 방지)
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.registeredBy ORDER BY b.createdAt DESC")
    List<Book> findAllWithMember();
}