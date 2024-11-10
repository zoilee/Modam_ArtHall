package com.arthall.modam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.arthall.modam.dto.UserDto;
import com.arthall.modam.service.UserService;

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
    public String registerUser(@ModelAttribute UserDto userDto, Model model) {
        // 필수 필드 검증
        if (userDto.getUserId() == null || userDto.getPassword() == null || userDto.getName() == null ||
            userDto.getEmail() == null || userDto.getPhoneNumber() == null ||
            userDto.getUserId().isEmpty() || userDto.getPassword().isEmpty() || 
            userDto.getName().isEmpty() || userDto.getEmail().isEmpty() || 
            userDto.getPhoneNumber().isEmpty()) {
            
            model.addAttribute("error", "모든 필수 항목을 입력해 주세요.");
            return "register";
        }

        // 회원가입 처리
        userService.registerUser(userDto);
        return "redirect:/login";
    }
}