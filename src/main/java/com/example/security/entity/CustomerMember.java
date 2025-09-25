package com.example.security.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
public class CustomerMember extends User {
    // 우리 애플리케이션의 회원 정보를 그대로 보관
    // View(Thymeleaf)에서 ${#authentication.principal.member.name} 형태로 접근 가능
    private Member member;

    public CustomerMember(Member member) {
        // 부모 클래스(User) 생성자 호출
        // User(username, password, authorities)
        super(member.getUsername(), member.getPassword(), getAuthorities(member.getRoles())); // 로그인 ID  // 암호화된 비밀번호 // 권한 정보 변환
        this.member=member;             // 추가 정보 저장 (이름, 나이, 이메일 등 View에서 필요한 정보)
    }

    /**
     * JPA Role 엔티티를 Spring Security의 GrantedAuthority로 변환
     *
     * 변환 예시:
     * - Role(name="USER") → SimpleGrantedAuthority("ROLE_USER")
     * - Role(name="ADMIN") → SimpleGrantedAuthority("ROLE_ADMIN")
     *
     * @param roles DB에서 조회한 Role 엔티티 집합
     * @return Spring Security가 인식하는 권한 객체 컬렉션
     */
    private static Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
                // "ROLE_" 접두사는 Spring Security의 규칙
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList()); // [ROLE_USER, ROLE_MANAGER, ROLE_ADMIN]
    }
}
