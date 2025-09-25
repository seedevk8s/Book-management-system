package com.example.security.config;

import com.example.security.entity.Book;
import com.example.security.entity.Member;
import com.example.security.entity.Role;
import com.example.security.repository.BookRepository;
import com.example.security.repository.MemberRepository;
import com.example.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 애플리케이션 시작 시 초기 데이터를 생성하는 클래스
 *
 * 개발 환경에서 테스트를 위한 기본 데이터 생성:
 * - Role 초기화 (USER, ADMIN)
 * - 테스트 사용자 생성
 * - 샘플 책 데이터 생성
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final BookRepository bookRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 초기 데이터 생성 시작 ===");

        // 1. Role 초기화
        Role userRole = createRoleIfNotExists("USER");
        Role adminRole = createRoleIfNotExists("ADMIN");

        // 2. 테스트 사용자 생성
        Member testUser = createMemberIfNotExists(
                "user", "user123", "김철수", 25, "user@test.com",
                Set.of(userRole)
        );

        Member adminUser = createMemberIfNotExists(
                "admin", "admin123", "관리자", 30, "admin@test.com",
                Set.of(userRole, adminRole)
        );

        // 3. 샘플 책 데이터 생성 (책이 하나도 없을 때만)
        if (bookRepository.count() == 0) {
            createSampleBooks(testUser, adminUser);
        }

        System.out.println("=== 초기 데이터 생성 완료 ===");
        printLoginInfo();
    }

    /**
     * Role이 없으면 생성
     */
    private Role createRoleIfNotExists(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            role = roleRepository.save(role);
            System.out.println("Role 생성됨: " + roleName);
        }
        return role;
    }

    /**
     * Member가 없으면 생성
     */
    private Member createMemberIfNotExists(String username, String password,
                                           String name, int age, String email,
                                           Set<Role> roles) {
        if (!memberRepository.findByUsername(username).isPresent()) {
            Member member = new Member();
            member.setUsername(username);
            member.setPassword(passwordEncoder.encode(password));
            member.setName(name);
            member.setAge(age);
            member.setEmail(email);
            member.setRoles(new HashSet<>(roles));

            member = memberRepository.save(member);
            System.out.println("회원 생성됨: " + username);
            return member;
        }
        return memberRepository.findByUsername(username).get();
    }

    /**
     * 샘플 책 데이터 생성
     */
    private void createSampleBooks(Member user, Member admin) {
        // 일반 사용자가 등록한 책
        Book book1 = new Book();
        book1.setTitle("자바의 정석");
        book1.setAuthor("남궁성");
        book1.setPrice(30000);
        book1.setPage(1022);
        book1.setDescription("자바 프로그래밍의 기초부터 실무까지");
        book1.setRegisteredBy(user);
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("스프링 부트와 AWS로 혼자 구현하는 웹 서비스");
        book2.setAuthor("이동욱");
        book2.setPrice(22000);
        book2.setPage(416);
        book2.setDescription("스프링 부트와 AWS를 이용한 웹 서비스 구축");
        book2.setRegisteredBy(user);
        bookRepository.save(book2);

        // 관리자가 등록한 책
        Book book3 = new Book();
        book3.setTitle("Clean Code");
        book3.setAuthor("로버트 마틴");
        book3.setPrice(33000);
        book3.setPage(464);
        book3.setDescription("깨끗한 코드를 작성하는 방법");
        book3.setRegisteredBy(admin);
        bookRepository.save(book3);

        System.out.println("샘플 책 3권이 생성되었습니다.");
    }

    /**
     * 로그인 정보 출력
     */
    private void printLoginInfo() {
        System.out.println("\n========================================");
        System.out.println("📌 테스트 계정 정보");
        System.out.println("========================================");
        System.out.println("일반 사용자:");
        System.out.println("  ID: user");
        System.out.println("  PW: user123");
        System.out.println("----------------------------------------");
        System.out.println("관리자:");
        System.out.println("  ID: admin");
        System.out.println("  PW: admin123");
        System.out.println("========================================\n");
    }
}