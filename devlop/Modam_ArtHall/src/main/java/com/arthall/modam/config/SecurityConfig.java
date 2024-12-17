package com.arthall.modam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import com.arthall.modam.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL); // Context 설정

        http
                .csrf(csrf -> csrf.disable()) // Lambda DSL로 CSRF 보호 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        // 누구나 접근 가능한 경로
                        .requestMatchers("/", "/hallDetail", "/findAccount", "/showDetail", "/user/api/**", "/userNoticeList",
                                "/noticeList", "/showDetail/{performanceId}", "/register", "/login", "/find-id",
                                "/find-password", "/css/**", "/js/**", "/imgs/**", "/showList", "/showListFragment",
                                "/error", "/uploads/**","/webhook/**")
                        .permitAll()
                        // 로그인 사용자만 접근 가능한 경로
                        .requestMatchers("/mypage", "/mypage/logs", "/reservForm", "/seatSelect", "/reservConfirm",
                                "/registeruserEdit")
                        .hasRole("USER")
                        // 관리자만 접근 가능한 경로
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 나머지는 모두 인증 필요
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login") // 사용자 정의 로그인 페이지
                        .loginProcessingUrl("/login") // 로그인 폼의 action URL
                        .failureUrl("/login?error=true") // 로그인 실패 시 이동할 URL
                        .failureHandler((request, response, exception) -> {
                            String errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";

                            // 계정 정지 상태 메시지 처리
                            if (exception.getMessage().contains("계정이 정지되었습니다")) {
                                errorMessage = "계정이 정지되었습니다. 관리자에게 문의하세요.";
                            }

                            request.getSession().setAttribute("loginError", errorMessage);
                            response.sendRedirect("/login");
                        })
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 이동할 기본 URL
                        .usernameParameter("username") // username 파라미터 설정
                        .passwordParameter("password")
                        .permitAll() // 로그인 페이지에 대한 접근 권한 설정 추가
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 메인 페이지로 이동
                        .invalidateHttpSession(true) // 세션 무효화
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // CustomOAuth2UserService 사용
                        ));

        return http.build();
    }

    // 비밀번호 암호화 빈 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
