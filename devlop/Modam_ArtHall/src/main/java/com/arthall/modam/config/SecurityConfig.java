package com.arthall.modam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL); // Context 설정
        
        http
            .csrf(csrf -> csrf.disable())  // Lambda DSL로 CSRF 보호 비활성화
            .authorizeHttpRequests(authorize -> authorize
            // 누구나 접근 가능한 경로
            .requestMatchers("/","/hallDetail", "/noticeList", "/showDetail", "/register", "/login", "/css/**", "/js/**", "/imgs/**").permitAll()
            // 로그인 사용자만 접근 가능한 경로
            .requestMatchers("/mypage", "/reservForm", "/seatSelect", "/reservConfirm", "/registeruserEdit").authenticated()
            // 관리자만 접근 가능한 경로
            .requestMatchers("/admin/**").hasRole("ADMIN")
            // 나머지는 모두 인증 필요
            .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")  // 사용자 정의 로그인 페이지
                .loginProcessingUrl("/login")  // 로그인 폼의 action URL
                .defaultSuccessUrl("/", true)  // 로그인 성공 시 이동할 기본 URL
                .failureUrl("/login?error=true")  // 로그인 실패 시 이동할 URL
                .usernameParameter("username") // username 파라미터 설정
                .passwordParameter("password")
                .permitAll()  // 로그인 페이지에 대한 접근 권한 설정 추가
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")  // 로그아웃 성공 시 메인 페이지로 이동
                .invalidateHttpSession(true)  // 세션 무효화
                .permitAll()
            );

        return http.build();
    }

    // 비밀번호 암호화 빈 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
