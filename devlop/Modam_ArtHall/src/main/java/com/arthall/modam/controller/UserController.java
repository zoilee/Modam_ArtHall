package com.arthall.modam.controller;





import java.security.Principal;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.arthall.modam.dto.UserDto;
import com.arthall.modam.entity.ReservationEntity;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.service.ReservationService;
import com.arthall.modam.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {
    @Autowired
    private final UserService userService;

    @Autowired
    private ReservationService reservationService;

    // @Autowired
    // private ReservationService reservationService;

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
        
        // 입력 유효성 검사: BindingResult를 통해 @Valid 어노테이션 기반 유효성 검사
        if(bindingResult.hasErrors()) {
            return "/register";
        }

        // 필수 필드 검증: 각 필드가 null 또는 빈 문자열인지 확인
        if (userDto.getLoginId() == null || userDto.getLoginId().isEmpty() ||
            userDto.getPassword() == null || userDto.getPassword().isEmpty() ||
            userDto.getName() == null || userDto.getName().isEmpty() ||
            userDto.getEmail() == null || userDto.getEmail().isEmpty() ||
            userDto.getPhoneNumber() == null || userDto.getPhoneNumber().isEmpty()) {
            
            model.addAttribute("error", "모든 필수 항목을 입력해 주세요.");
            return "register";
        }

        // 회원가입 처리
        userService.registerUser(userDto);
        return "redirect:/login";
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

    @GetMapping("/mypage")
    public String mypage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // 인증되지 않은 경우 로그인 페이지로 리다이렉트
        }
    
        String loginId = principal.getName();
        UserEntity user = userService.getUserByLoginId(loginId);
        int userId = user.getId();
    
        // 예: 예약 데이터 가져오기 (서비스 계층 메서드 필요)
        List<ReservationEntity> upcomingReservations = reservationService.getUpcomingReservationsByUserId(userId);
        List<ReservationEntity> pastReservations = reservationService.getPastReservationsByUserId(userId);

        // 최신 날짜 순으로 정렬
        upcomingReservations.sort(Comparator.comparing(ReservationEntity::getReservationDate).reversed());
        pastReservations.sort(Comparator.comparing(ReservationEntity::getReservationDate).reversed());
    
        
    
        int points = userService.getUserPointsById(userId);
    
        model.addAttribute("user", user);
        model.addAttribute("points", points);
        model.addAttribute("upcomingReservations", upcomingReservations);
        model.addAttribute("pastReservations", pastReservations);

        // 예약 데이터가 없을 경우의 메시지 설정
        if (upcomingReservations.isEmpty()) {
            model.addAttribute("noUpcomingReservationsMessage", "현재 예약이 없습니다.");
        }
        if (pastReservations.isEmpty()) {
            model.addAttribute("noPastReservationsMessage", "과거 예약이 없습니다.");
        }
    
        return "mypage";
    }

}
