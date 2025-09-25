package com.example.security.service;

import com.example.security.entity.Book;
import com.example.security.entity.Member;
import com.example.security.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 책 관련 비즈니스 로직을 처리하는 서비스
 *
 * 주요 기능:
 * - 책 CRUD 작업
 * - 현재 로그인한 사용자 정보 자동 연결
 * - 트랜잭션 관리
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // 기본적으로 읽기 전용 트랜잭션
public class BookService {

    private final BookRepository bookRepository;
    private final MemberService memberService;

    /**
     * 책 등록
     * 현재 로그인한 사용자를 자동으로 등록자로 설정
     */
    @Transactional  // 쓰기 작업이므로 readOnly=false
    public Book register(Book book) {
        // 현재 로그인한 사용자 정보 가져오기
        String username = getCurrentUsername();
        Member currentMember = memberService.findByUsername(username);

        // 책 등록자 설정
        book.setRegisteredBy(currentMember);

        return bookRepository.save(book);
    }

    /**
     * 책 수정
     * 본인이 등록한 책만 수정 가능하도록 체크
     */
    @Transactional
    public Book update(Long id, Book updateBook) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다: " + id));

        // 권한 체크: 본인이 등록한 책인지 확인
        String currentUsername = getCurrentUsername();
        if (!book.getRegisteredBy().getUsername().equals(currentUsername)) {
            throw new SecurityException("본인이 등록한 책만 수정할 수 있습니다.");
        }

        // 수정 가능한 필드만 업데이트
        book.setTitle(updateBook.getTitle());
        book.setPrice(updateBook.getPrice());
        book.setAuthor(updateBook.getAuthor());
        book.setPage(updateBook.getPage());
        book.setDescription(updateBook.getDescription());

        return bookRepository.save(book);
    }

    /**
     * 책 삭제
     * 본인이 등록한 책 또는 ADMIN 권한자만 삭제 가능
     */
    @Transactional
    public void delete(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다: " + id));

        String currentUsername = getCurrentUsername();
        boolean isAdmin = hasAdminRole();

        // 권한 체크: 본인이 등록했거나 관리자인 경우만 삭제 가능
        if (!book.getRegisteredBy().getUsername().equals(currentUsername) && !isAdmin) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        bookRepository.delete(book);
    }

    /**
     * 모든 책 조회 (최신순)
     */
    public List<Book> findAll() {
        return bookRepository.findAllWithMember();
    }

    /**
     * ID로 책 조회
     */
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다: " + id));
    }

    /**
     * 제목으로 책 검색
     */
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * 현재 로그인한 사용자가 등록한 책 목록
     */
    public List<Book> findMyBooks() {
        String username = getCurrentUsername();
        Member currentMember = memberService.findByUsername(username);
        return bookRepository.findByRegisteredBy(currentMember);
    }

    /**
     * 현재 로그인한 사용자명 가져오기
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("로그인이 필요합니다.");
        }
        return authentication.getName();
    }

    /**
     * 현재 사용자가 ADMIN 권한을 가지고 있는지 확인
     */
    private boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}