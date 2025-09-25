package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 핵심 설정 클래스
 *
 * 이 클래스의 목적:
 * 1. 애플리케이션의 보안 정책 중앙 관리
 * 2. 인증(Authentication)과 인가(Authorization) 규칙 설정
 * 3. 비밀번호 암호화 방식 정의
 * 4. CSRF, CORS, Session 관리 등 보안 관련 설정
 */
@Configuration              // Spring 설정 클래스임을 명시 (Bean 정의를 포함)
@EnableWebSecurity          // Spring Security 활성화 (Spring Boot 2.x 이상에서는 생략 가능)
public class SecurityConfig {

    /**
     * 비밀번호 암호화기 Bean 등록
     *
     * BCrypt 알고리즘 사용 이유:
     * - 단방향 해시 함수로 복호화 불가능
     * - Salt를 자동으로 생성하여 레인보우 테이블 공격 방어
     * - 강도(strength) 조절 가능 (기본값: 10)
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
        // 강도 조절 예시: new BCryptPasswordEncoder(12);
    }

    /**
     * Spring Security 필터 체인 설정
     *
     * SecurityFilterChain의 역할:
     * - HTTP 요청에 대한 보안 규칙 정의
     * - 인증/인가 처리 순서 결정
     * - 각종 보안 기능 활성화/비활성화
     *
     * @param http HttpSecurity 객체 (보안 설정 빌더)
     * @return 구성된 SecurityFilterChain
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        // TODO: 실제 보안 설정 추가 필요
        // 현재는 기본 설정만 적용됨 (모든 요청에 인증 필요)
        http
                // ====== 1. URL별 접근 권한 설정 ======
                .authorizeHttpRequests(authz -> authz
                        // /api/** 경로: 인증된 사용자만 접근 가능 (로그인 필수)
                        .requestMatchers("/api/**").authenticated()

                        // /book/** 경로: 인증된 사용자만 접근 가능 (책 관련 기능)
                        .requestMatchers("/book/**").authenticated()

                        // 그 외 모든 요청: 인증 없이 접근 가능 (회원가입, 메인페이지 등)
                        .anyRequest().permitAll()
                )

                // ====== 2. 폼 로그인 설정 ======
                .formLogin(form -> form
                        // 커스텀 로그인 페이지 지정 (기본값: /login)
                        // 로그인이 필요한 경우 이 경로로 리다이렉트
                        .loginPage("/ui/list")

                        // 실제 로그인 처리 URL (form의 action 속성값)
                        // 이 URL로 POST 요청 시 Spring Security가 인증 처리
                        .loginProcessingUrl("/login")

                        // 로그인 성공 후 이동할 기본 페이지
                        // true: 항상 이 페이지로 이동 (이전 페이지 무시)
                        // false: 로그인 전 요청했던 페이지로 이동
                        .defaultSuccessUrl("/ui/list", true)
                )

                // ====== 3. 로그아웃 설정 ======
                .logout(logout -> logout
                        // 로그아웃 처리 URL (이 URL로 POST 요청 시 로그아웃)
                        .logoutUrl("/logout")

                        // 로그아웃 성공 후 리다이렉트할 페이지
                        .logoutSuccessUrl("/ui/list")

                        // SecurityContext의 Authentication 객체 제거
                        .clearAuthentication(true)

                        // 세션 쿠키 삭제 (브라우저의 세션 식별자 제거)
                        .deleteCookies("JSESSIONID")

                        // 서버의 HttpSession 무효화 (세션 데이터 완전 삭제)
                        .invalidateHttpSession(true)
                );
    //    HttpSecurity (빌더)
    //        SecurityFilterChain을 만들기 위한 설정 도구
    //        다양한 보안 설정 메서드 제공
    //        메서드 체이닝으로 가독성 향상
    //    build() 메서드
    //        최종적으로 SecurityFilterChain 객체 생성
    //        설정된 모든 필터들을 체인으로 연결
    //        Spring Security가 이 체인을 통해 요청을 처리
        return http.build();
    }

}