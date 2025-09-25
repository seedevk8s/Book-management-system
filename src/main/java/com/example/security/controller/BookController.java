package com.example.security.controller;

import com.example.security.entity.Book;
import com.example.security.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 책 관련 웹 요청을 처리하는 컨트롤러
 *
 * URL 매핑:
 * - /book/** : 모든 책 관련 요청
 * - Spring Security 설정에서 인증된 사용자만 접근 가능
 */
@Controller
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 책 목록 조회 (메인 화면에 통합)
     */
    @GetMapping("/list")
    public String list(Model model) {
        List<Book> books = bookService.findAll();
        model.addAttribute("books", books);
        return "redirect:/ui/list";  // 메인 화면으로 리다이렉트
    }

    /**
     * 책 등록 폼 표시
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("book", new Book());
        return "book/register";  // book/register.html
    }

    /**
     * 책 등록 처리
     */
    @PostMapping("/register")
    public String register(@ModelAttribute Book book,
                           RedirectAttributes redirectAttributes) {
        try {
            bookService.register(book);
            redirectAttributes.addFlashAttribute("successMessage",
                    "책이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "책 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/ui/list";
    }

    /**
     * 책 상세 조회
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        return "book/detail";  // book/detail.html
    }

    /**
     * 책 수정 폼 표시
     */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        try {
            Book book = bookService.findById(id);
            model.addAttribute("book", book);
            return "book/edit";  // book/edit.html
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/ui/list";
        }
    }

    /**
     * 책 수정 처리
     */
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute Book book,
                       RedirectAttributes redirectAttributes) {
        try {
            bookService.update(id, book);
            redirectAttributes.addFlashAttribute("successMessage",
                    "책 정보가 수정되었습니다.");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/ui/list";
    }

    /**
     * 책 삭제 처리
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            bookService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "책이 삭제되었습니다.");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/ui/list";
    }

    /**
     * 내가 등록한 책 목록
     */
    @GetMapping("/mybooks")
    public String myBooks(Model model) {
        List<Book> myBooks = bookService.findMyBooks();
        model.addAttribute("books", myBooks);
        model.addAttribute("pageTitle", "내가 등록한 책");
        return "book/list";  // book/list.html
    }

    /**
     * 책 검색
     */
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String title,
                         Model model) {
        if (title != null && !title.trim().isEmpty()) {
            List<Book> books = bookService.searchByTitle(title);
            model.addAttribute("books", books);
            model.addAttribute("searchKeyword", title);
        }
        return "book/search";  // book/search.html
    }
}