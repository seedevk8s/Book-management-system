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
     *
     *
    ✅ 개발자가 할 일:

             @EnableWebSecurity 어노테이션 추가
             @Bean 메서드에서 HttpSecurity 파라미터 선언
             필요한 설정 추가
             build() 호출

     ✅ Spring이 자동으로 해주는 일:

             HttpSecurity 객체 생성
             Builder 패턴 구현
             기본 설정 적용
             개발자 메서드에 자동 주입
             SecurityFilterChain으로 변환

     결론: 네, 맞습니다! 개발자는 메서드 파라미터로 선언만 하면 Spring이 모든 것을 준비해줍니다! 🎯     *





             // Spring은 타입(Type)으로 주입합니다!
             // 이름이 아니라 HttpSecurity 타입을 보고 주입

             // Spring: "아, HttpSecurity 타입이 필요하구나!"
             // Spring: "HttpSecurity 빈을 찾아서 주입하자!"
                // 개발자: "HttpSecurity http 파라미터로 선언할게요!"
      ** 왜 파라미터에 참조 변수이름으로 'http'를 많이 사용할까?
             // 이유 1: Spring Security 공식 문서가 http 사용
             // 이유 2: 짧고 직관적
             // 이유 3: HTTP 보안 설정임을 암시
             // 이유 4: 관례(Convention)
             // 'http'는 HTTP 요청에 대한 보안 설정임을 직관적으로 표현

             결론:
             ✅ 파라미터 이름은 자유롭게 정할 수 있습니다
             ✅ Spring은 타입(HttpSecurity)으로 주입하지, 이름으로 하지 않습니다
             ✅ http는 단지 관례일 뿐입니다
             ✅ 팀의 코딩 컨벤션에 맞춰 일관성 있게 사용하면 됩니다
     */
    // 2️⃣ 웹 애플리케이션 (MVC)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        // Spring이 주입해준 http 객체를 받아서
        // 추가 설정만 하면 됨!

        // TODO: 실제 보안 설정 추가 필요
        // 처음엔 기본 설정만 적용됨 (모든 요청에 인증 필요-기본 보안 설정으로 동작)
        http
                /*
                    // 각 설정은 특정 Security Filter를 생성합니다
                        http.authorizeHttpRequests()  → AuthorizationFilter
                        http.formLogin()              → UsernamePasswordAuthenticationFilter
                        http.logout()                 → LogoutFilter
                 */
              /*
                    // ✅ 기준 1: 필수 설정
                    http.authorizeHttpRequests(authz -> authz
                        // 거의 모든 앱에서 필요한 URL 접근 제어
                        .anyRequest().authenticated()
                    );

                    // ✅ 기준 2: 인증 방식 선택 (최소 하나는 필요)
                    http.formLogin();     // 폼 기반 로그인
                    // 또는
                    http.httpBasic();     // HTTP Basic 인증
                    // 또는
                    http.oauth2Login();   // OAuth2 로그인

                    // ✅ 기준 3: 선택적 설정 (필요에 따라)
                    http.logout();        // 로그아웃 (보통 필요)
                    http.csrf();          // CSRF 보호 (REST API면 disable)
                    http.cors();          // Cross-Origin 요청 허용

                    return http.build();
               */

                // ====== 1. URL별 접근 권한 설정 ======
                // 🔐 HTTP 요청 인증/인가 설정
                .authorizeHttpRequests(authz -> authz          // authorizeRequests()는 deprecated됨
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
                        /*
                            // 내부적으로 이런 일들이 발생:
                            1. 로그인 페이지 경로 저장
                            2. AuthenticationEntryPoint 생성 (인증 필요시 여기로 리다이렉트)
                            3. 해당 경로에 대한 접근 권한 자동 설정 (permitAll)
                            4. 로그인 폼 제출 경로 기본값 설정 (별도 지정 안하면 같은 경로로 POST)
                         */                                    // 👉 GET 요청: 우리가 만든 페이지
                        .loginPage("/ui/list")              // ✅ 메서드 이름 고정, 파라미터만 변경    // Spring Security가 정한 이름  🟢 변경 가능 (파라미터 값)

                        // 실제 로그인 처리 URL (form의 action 속성값)
                        // 이 URL로 POST 요청 시 Spring Security가 인증 처리
                        /*
                        // 2. Spring Security의 UsernamePasswordAuthenticationFilter가 자동으로 가로채서 처리
                        // 우리가 만든 컨트롤러가 아님!
                         */
                        .loginProcessingUrl("/login")  // ✅ 메서드 이름 고정   // Spring Security가 정한 이름  // 👉 POST 요청: Spring Security가 자동 처리!

                        // 로그인 성공 후 이동할 기본 페이지
                        // true: 항상 이 페이지로 이동 (이전 페이지 무시)
                        // false: 로그인 전 요청했던 페이지로 이동
                        .defaultSuccessUrl("/ui/list", true)    // ✅ 메서드 이름 고정      // Spring Security가 정한 이름
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