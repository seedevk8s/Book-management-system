package com.example.security.controller;

import com.example.security.entity.Book;
import com.example.security.entity.Member;
import com.example.security.service.BookService;
import com.example.security.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final BookService bookService;  // BookService 추가

    @GetMapping("/register")
    public String register(){
        return "register"; // register.html
    }

    @PostMapping("/register")
    public String register(Member member){
        // 패스워드 암호화
        memberService.register(member); // 등록
        return "redirect:/ui/list"; // Book 목록 페이지로 이동
    }

    /**
     * 메인 페이지 - 책 목록 포함
     */
    @GetMapping("/ui/list")
    public String main(Model model){
        try {
            // 책 목록을 조회하여 모델에 추가
            List<Book> books = bookService.findAll();
            model.addAttribute("books", books);
        } catch (Exception e) {
            // 책 서비스가 아직 준비되지 않은 경우 빈 목록 전달
            model.addAttribute("books", List.of());
        }
        return "list"; // list.html
    }
}