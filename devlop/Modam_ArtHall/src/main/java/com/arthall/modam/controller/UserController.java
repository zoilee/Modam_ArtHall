package com.arthall.modam.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.arthall.modam.dto.UserDto;
import com.arthall.modam.entity.NoticesEntity;
import com.arthall.modam.service.BbsService;
import com.arthall.modam.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {
    @Autowired
    private final UserService userService;

    @Autowired
    private BbsService bbsService;


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


/********************************************************** 공지사항 *******************************************************************/

    
    // 일반 사용자용 공지사항 목록 조회 (페이지네이션 적용)
    @GetMapping("/userNoticeList")
    public String showUserNoticeList(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 5; // 페이지당 표시할 공지사항 수
        Page<NoticesEntity> notices = bbsService.getNotices(page, pageSize);
        
        model.addAttribute("notices", notices); // Page 객체 자체를 모델에 추가
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notices.getTotalPages()); // 전체 페이지 수 전달
        
        return "userNoticeList"; // 일반 사용자용 템플릿 반환
    }

    // 공지사항 상세 조회 페이지
    @GetMapping("/userNoticeView")
    public String showNoticeView(@RequestParam("id") int id, Model model) {
        NoticesEntity notice = bbsService.getNoticeById(id).orElse(null);
        if (notice == null) {
            return "redirect:/userNoticeList"; // 공지사항이 없을 경우 목록으로 리다이렉트
        }
        model.addAttribute("notice", notice);
        return "userNoticeView"; // 일반 사용자용 템플릿 반환
    }









    // @GetMapping("/mypage")
    // public String mypage(Model model) {
    //     int userId = 1; // 예시 사용자 ID, 실제로는 인증된 사용자 ID 사용
    //     UserEntity user = userService.getUserById(userId);

    //     if (user == null) {
    //         throw new RuntimeException("User not found with ID: " + userId);
    //     }

    //     List<ReservationEntity> upcomingReservations = reservationService.getUpcomingReservationsByUserId(userId);
    //     List<ReservationEntity> pastReservations = reservationService.getPastReservationsByUserId(userId);

    //     model.addAttribute("user", user);
    //     model.addAttribute("upcomingReservations", upcomingReservations);
    //     model.addAttribute("pastReservations", pastReservations);

    //     return "mypage"; // mypage.html로 이동
    // }
}