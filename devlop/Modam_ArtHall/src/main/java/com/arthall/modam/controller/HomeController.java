package com.arthall.modam.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.arthall.modam.entity.CommentEntity;
import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.entity.ReservationEntity;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.repository.PerformancesRepository;
import com.arthall.modam.service.CommentService;
import com.arthall.modam.service.PerformanceService;
import com.arthall.modam.service.ReservationService;
import com.arthall.modam.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private PerformanceService performanceService;

    @Autowired
    private PerformancesRepository performancesRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/")
    public String home() {
        return "main";
    }

    @GetMapping("/mypage")
    public String mypage(Model model) {
        // 현재 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login"; // 인증되지 않은 경우 로그인 페이지로 리다이렉트
        }
    
        Object principal = authentication.getPrincipal();
        String loginId = null;
    
        if (principal instanceof OAuth2User) {
            Map<String, Object> attributes = ((OAuth2User) principal).getAttributes();
            loginId = (String) attributes.get("loginId");
        } else if (principal instanceof UserDetails) {
            loginId = ((UserDetails) principal).getUsername();
        } else {
            throw new RuntimeException("인증된 사용자가 OAuth2User 또는 UserDetails가 아닙니다.");
        }
    
        if (loginId == null) {
            throw new RuntimeException("loginId를 찾을 수 없습니다.");
        }
    
        // loginId로 사용자 조회
        UserEntity user = userService.getUserByLoginId(loginId);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다: loginId = " + loginId);
        }
    
        int userId = user.getId();
    
        // 예약 데이터 가져오기
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
    
        if (upcomingReservations.isEmpty()) {
            model.addAttribute("noUpcomingReservationsMessage", "현재 예약이 없습니다.");
        }
        if (pastReservations.isEmpty()) {
            model.addAttribute("noPastReservationsMessage", "과거 예약이 없습니다.");
        }
    
        return "mypage";
    }


    @GetMapping("/registeruserEdit")
    public String registeruserEdit() {
        return "registeruserEdit";
    }


    // /showList 매핑
    @GetMapping("/showList")
    public String showList(Model model) {
        Date currentDate = Date.valueOf(LocalDate.now());
    
        List<PerformancesEntity> currentPerformances = performanceService.getUpcomingPerformances(currentDate);
        List<PerformancesEntity> pastPerformances = performanceService.getFinishedPerformances(currentDate);
    
        // 디버깅용 로그 추가
        System.out.println("Current Performances: " + currentPerformances);
        System.out.println("Past Performances: " + pastPerformances);
    
        model.addAttribute("currentPerformances", currentPerformances);
        model.addAttribute("pastPerformances", pastPerformances);
    
        return "showList";
    }


    

    @GetMapping("/showDetail/{performanceId}")
    public String showPerformanceDetails(@PathVariable("performanceId") int performanceId, Model model) {
        // 공연 정보 가져오기
        PerformancesEntity performance = performanceService.getPerformanceWithAverageRating(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("공연을 찾을 수 없습니다."));

        UserEntity user = null;
        boolean isAdmin = false; // 관리자 여부 확인
        String loginId = null;

        // 로그인 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof OAuth2User) {
                Map<String, Object> attributes = ((OAuth2User) principal).getAttributes();
                loginId = (String) attributes.get("loginId"); // CustomOAuth2UserService에서 추가된 loginId
            } else if (principal instanceof UserDetails) {
                loginId = ((UserDetails) principal).getUsername();
            } else {
                loginId = authentication.getName();
            }

            // 사용자 정보 조회
            if (loginId != null) {
                user = userService.getUserByLoginId(loginId);
                isAdmin = userService.isAdmin(user); // ROLE_ADMIN 확인
            }
        }

        // 사용자 정보 추가
        if (user != null) {
            model.addAttribute("userId", user.getId());
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("userId", 0); // 비로그인 사용자는 ID를 0으로 설정
            model.addAttribute("isAdmin", false); // 비로그인 사용자는 관리자 아님
        }

        // 댓글 및 총 댓글 수 가져오기
        List<CommentEntity> comments = commentService.getComments(performance, 0, 5);
        int totalComments = commentService.getTotalCommentsByPerformanceId(performanceId);

        // 모델에 데이터 추가
        model.addAttribute("performance", performance);
        model.addAttribute("comments", comments);
        model.addAttribute("totalComments", totalComments);

        return "showDetail"; // Thymeleaf 템플릿 이름
    }

    @PostMapping("/showDetail/addComment")
    @ResponseBody
    public String addComment(
            @RequestParam("performanceId") int performanceId,
            @RequestParam("commentText") String commentText,
            @RequestParam("rating") int rating) {
        // 로그인 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "error: 로그인 후 이용 가능합니다."; // 비로그인 사용자 처리
        }

        String loginId = authentication.getName();
        UserEntity user = userService.getUserByLoginId(loginId);

        PerformancesEntity performance = new PerformancesEntity();
        performance.setId(performanceId);

        commentService.addComment(user, performance, commentText, rating);
        return "success";
    }

    @PostMapping("/showDetail/updateComment")
    @ResponseBody
    public String updateComment(
            @RequestParam("commentId") int commentId,
            @RequestParam("commentText") String commentText) {
        // 로그인 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "error: 로그인 후 이용 가능합니다."; // 비로그인 사용자 처리
        }

        commentService.updateComment(commentId, commentText);
        return "success";
    }

    @PostMapping("/showDetail/deleteComment")
    @ResponseBody
    public String deleteComment(@RequestParam("commentId") int commentId) {
        // 로그인 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "error: 로그인 후 이용 가능합니다."; // 비로그인 사용자 처리
        }

        commentService.deleteComment(commentId);
        return "success";
    }

    @GetMapping("/showDetail/loadComments")
    @ResponseBody
    public List<CommentEntity> loadMoreComments(
            @RequestParam("performanceId") int performanceId,
            @RequestParam("offset") int offset) {
        PerformancesEntity performance = new PerformancesEntity();
        performance.setId(performanceId);
        return commentService.getComments(performance, offset, 5);
    }

    @GetMapping("/hallDetail")
    public String hallDetail() {
        return "hallDetail";
    }

    @GetMapping("/noticeList")
    public String noticeList() {
        return "noticeList";
    }

    @GetMapping("/noticeView")
    public String noticeView() {
        return "noticeView";
    }

    @GetMapping("/seatSelect")
    public String seatSelect() {
        return "seatSelect";
    }

    @GetMapping("/reservConfirm")
    public String reservConfirm() {
        return "reservConfirm";
    }

    @GetMapping("/reservForm")
    public String reservForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인되지 않은 경우
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("alertMessage", "로그인 후 이용하세요.");
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        // 로그인된 경우 예약 폼 페이지로 이동
        return "reservForm";
    }

    // 사용자 역할을 반환하는 유틸리티 메서드
    private String getUserRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 사용자의 첫 번째 권한을 가져옴 (ROLE_USER, ROLE_ADMIN 등)
        if (authorities != null && !authorities.isEmpty()) {
            return authorities.iterator().next().getAuthority();
        }
        return "ROLE_ANONYMOUS"; // 기본값
    }

    @GetMapping("/testmail")
    public String testMail(Model model) {

        return "testMail";
    }
}
