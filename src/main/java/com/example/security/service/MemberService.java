package com.example.security.service;

import com.example.security.entity.Member;
import com.example.security.entity.Role;
import com.example.security.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private  final RoleService roleService;

    public Member register(Member member){
        // 1. 사용자가 입력한 패스워드를 암호화
        // BCrypt 알고리즘으로 평문 패스워드를 암호화
        // 예: "1234" → "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"
        String pwd=passwordEncoder.encode(member.getPassword());
        member.setPassword(pwd);
        // 2. 회원의 기본 권한(USER, MANAGER, ADMIN)을 저장한다.
        Role userRole=roleService.findByName("USER");
        // Set 컬렉션 생성 (중복 권한 방지)
        // HashSet 사용 이유:
        // 1) 권한 중복 자동 방지
        // 2) 순서 불필요 (권한에 우선순위 없음)
        // 3) 빠른 검색 성능 O(1)
        Set<Role> roles=new HashSet<>();
        roles.add(userRole);        // USER 권한 추가
        member.setRoles(roles);     // Member 엔티티에 권한 설정    // @ManyToMany 관계로 member_roles 중간 테이블에 자동 저장됨
        // ============================================================
        // 3. DB 저장 및 반환
        // ============================================================

        // JPA Repository를 통해 DB 저장
        // 저장 과정:
        // 1) member 테이블에 회원 정보 INSERT
        // 2) member_roles 테이블에 회원-권한 관계 INSERT (자동)
        // 3) 저장된 엔티티 반환 (ID가 생성된 상태)
        return memberRepository.save(member);
        /*
         * 🚀 트랜잭션 처리:
         * - @Transactional이 없어도 SimpleJpaRepository에서 자동 처리
         * - save() 메서드는 내부적으로 @Transactional 적용됨
         * - 실패 시 자동 롤백
         */
    }

    /**
     * username으로 회원 조회 메서드
     * 주로 로그인 시 CustomUserDetailsService에서 호출
     *
     * @param username 조회할 회원의 username (로그인 ID)
     * @return 조회된 Member 엔티티
     * @throws UsernameNotFoundException 회원을 찾을 수 없을 때
     */
    // ============================================================
    // Repository를 통한 회원 조회
    // ============================================================

    // JPA Repository 메서드 호출
    // findByUsername()은 Spring Data JPA가 자동 생성하는 쿼리 메서드
    // 실행되는 SQL: SELECT * FROM member WHERE username = ?
    public Member findByUsername(String username){
        Optional<Member> optional =memberRepository.findByUsername(username);
        if(!optional.isPresent()){
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return optional.get();  // Optional에서 실제 Member 객체 추출하여 반환
    }
}


/*
 * ============================================================
 * 🎯 이 서비스 클래스의 핵심 역할:
 * ============================================================
 *
 * 1. 회원가입 (register)
 *    - 비밀번호 암호화 (보안)
 *    - 기본 권한 설정 (인가)
 *    - DB 저장 (영속성)
 *
 * 2. 회원 조회 (findByUsername)
 *    - 로그인 시 사용
 *    - CustomUserDetailsService와 연동
 *    - 예외 처리 표준화
 *
 * ============================================================
 * 🔄 호출 흐름:
 * ============================================================
 *
 * [회원가입]
 * MemberController → MemberService.register() → MemberRepository.save()
 *
 * [로그인]
 * Spring Security → CustomUserDetailsService → MemberService.findByUsername()
 *
 * ============================================================
 * ⚠️ 주의사항:
 * ============================================================
 *
 * 1. Role 테이블에 권한 데이터가 미리 있어야 함
 *    INSERT INTO role (name) VALUES ('USER'), ('ADMIN'), ('MANAGER');
 *
 * 2. username은 unique 제약조건이 있어야 함
 *    중복 체크 로직 추가 권장
 *
 * 3. 패스워드는 절대 평문으로 저장하지 않음
 *    항상 BCrypt 등으로 암호화
 *
 * ============================================================
 */