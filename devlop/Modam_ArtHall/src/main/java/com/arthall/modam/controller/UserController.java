package com.arthall.modam.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.arthall.modam.dto.UserDto;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {
    
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 폼 표시
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "register";
    }

    // HTML 회원가입 처리
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDto") UserDto userDto, BindingResult bindingResult, Model model) {

        // 회원가입 처리
        userService.registerUser(userDto);
        return "redirect:/login"; // 성공 시 로그인 페이지로 이동
    }

    @GetMapping("/user/api/check-login-id")
    public ResponseEntity<Map<String, Boolean>> checkLoginId(@RequestParam(name = "loginId") String loginId) {
    boolean exists = userService.isLoginIdDuplicate(loginId);
    return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/user/api/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam(name = "email") String email) {
    boolean exists = userService.isEmailDuplicate(email);
    return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/user/api/check-phone")
    public ResponseEntity<Map<String, Boolean>> checkPhoneNumber(@RequestParam(name = "phone") String phoneNumber) {
    boolean exists = userService.isPhoneNumberDuplicate(phoneNumber);
    return ResponseEntity.ok(Map.of("exists", exists));
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

    // 개인정보 수정 폼 표시
    @GetMapping("/registeruserEdit")
    public String showRegisterUserEditPage(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        // 로그인된 사용자 정보 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String loginId = userDetails.getUsername();

        // 사용자 정보를 모델에 추가
        UserEntity userDto = userService.getUserByLoginId(loginId);
        model.addAttribute("user", userDto); // "user"라는 이름으로 모델에 추가

        return "registeruserEdit"; // 수정 페이지로 이동
    }

    // 개인정보 수정 처리
    @PostMapping("/registeruserEdit")
    public String updateUserInfo(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult, HttpSession session, Model model) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) {
            return "redirect:/login"; // 로그인하지 않은 사용자는 로그인 페이지로 리다이렉트
        }

        // 현재 로그인된 아이디를 설정
        userDto.setLoginId(loginId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "유효성 검증에 실패했습니다. 입력값을 확인해주세요.");
            return "registeruserEdit"; // 유효성 검증 실패 시 수정 페이지로 다시 이동
        }

        try {
            userService.updateUserInfo(userDto); // 사용자 정보 업데이트
            model.addAttribute("success", "개인정보가 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "registeruserEdit";
        }

        return "redirect:/"; // 수정 성공 후 메인 페이지로 이동
    }

}
