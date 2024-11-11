package com.arthall.modam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.arthall.modam.dto.UserDto;
import com.arthall.modam.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 폼 표시
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "register";
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String registerUser(@Valid UserDto userDto, BindingResult bindingResult, Model model) {
        
        if(bindingResult.hasErrors()){

            return "/register";
        }

        userService.registerUser(userDto);
        return "redirect:/login";
        
        // 필수 필드 검증
        // if (userDto.getLoginId() == null || userDto.getPassword() == null || userDto.getName() == null ||
        //     userDto.getEmail() == null || userDto.getPhoneNumber() == null ||
        //     userDto.getLoginId().isEmpty() || userDto.getPassword().isEmpty() || 
        //     userDto.getName().isEmpty() || userDto.getEmail().isEmpty() || 
        //     userDto.getPhoneNumber().isEmpty()) {
            
        //     model.addAttribute("error", "모든 필수 항목을 입력해 주세요.");
        //     return "register";
        // }

        // // 회원가입 처리
        // userService.registerUser(userDto);
        // return "redirect:/login";
    }
    // 로그인 폼 표시
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String loginUser(@ModelAttribute UserDto userDto, HttpSession session, Model model) {
        boolean isAuthenticated = userService.login(userDto.getLoginId(), userDto.getPassword());
        
        if (isAuthenticated) {
            // 로그인 성공 시 세션에 사용자 정보 저장
            session.setAttribute("loginId", userDto.getLoginId());
            return "redirect:/";  // 로그인 후 이동할 페이지로 리다이렉트
        } else {
            model.addAttribute("error", "로그인 정보가 올바르지 않습니다.");
            return "login";  // 로그인 실패 시 다시 로그인 페이지로 이동
        }
    }
}