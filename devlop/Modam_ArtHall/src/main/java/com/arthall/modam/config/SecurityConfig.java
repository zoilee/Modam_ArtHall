package com.arthall.modam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Lambda DSL로 CSRF 보호 비활성화
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/noticeList", "/showDetail", "/reservForm", "/noticeView", "/register", "/login", "/css/**", "/js/**", "/imgs/**").permitAll()  // 로그인, 회원가입 페이지와 정적 리소스는 모두 접근 가능하게 설정
                .anyRequest().permitAll() //인증없이 모든 페이지
            )
            .formLogin(form -> form
                .loginPage("/login")  // 사용자 정의 로그인 페이지
                .loginProcessingUrl("/login")  // 로그인 폼의 action URL
                .defaultSuccessUrl("/", true)  // 로그인 성공 시 이동할 기본 URL
                .failureUrl("/login?error=true")  // 로그인 실패 시 이동할 URL
                .permitAll()  // 로그인 페이지에 대한 접근 권한 설정 추가
                .usernameParameter("loginId")  // 로그인 아이디 파라미터 이름 설정
                .passwordParameter("password")  // 비밀번호 파라미터 이름 설정
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")  // 로그아웃 성공 시 이동할 URL
                .invalidateHttpSession(true)  // 세션 무효화
            );
        
        return http.build();
    }

    // 비밀번호 암호화 빈 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
