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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Spring Security 6.1 버전부터 csrf().disable() 메서드가 더 이상 권장되지 않으며 제거될 예정이라는 경고 메시지
    //실행해도 문제는 없으나 경고는 계속 뜰 수 있음
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/**").permitAll() // 모든 요청에 대해 인증 없이 접근 허용
            )
            .csrf().disable(); // CSRF 보호 비활성화 (개발 중 일시적으로 비활성화)

        return http.build();
    }


}