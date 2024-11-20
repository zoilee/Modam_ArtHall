package com.arthall.modam.config;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("userRole")
    public String addUserRoleToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            return authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(role -> role.startsWith("ROLE_")) // ROLE_ADMIN, ROLE_USER 등만 필터링
                    .findFirst()
                    .orElse("ROLE_USER"); // 기본값: ROLE_USER
        }
        return null; // 로그인하지 않은 사용자
    }

    @ModelAttribute("username")
    public String addUsernameToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName(); // 인증된 사용자 이름 반환
        }
        return null;
    }
}