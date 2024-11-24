package com.arthall.modam.controller;

import java.util.List;

import java.util.Map;
import java.security.Principal;

import java.sql.Date;

import java.time.LocalDate;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

import java.util.Optional;

import java.text.SimpleDateFormat;
import java.util.Calendar;



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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arthall.modam.entity.CommentEntity;
import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.entity.ReservationsEntity;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.repository.PerformancesRepository;
import com.arthall.modam.service.CommentService;
import com.arthall.modam.service.PerformanceService;
import com.arthall.modam.service.ReservationsService;
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
    private ReservationsService reservationService;

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
        List<ReservationsEntity> upcomingReservations = reservationService.getUpcomingReservationsByUserId(userId);
        List<ReservationsEntity> pastReservations = reservationService.getPastReservationsByUserId(userId);

        // 최신 날짜 순으로 정렬
        upcomingReservations.sort(Comparator.comparing(ReservationsEntity::getReservationDate).reversed());
        pastReservations.sort(Comparator.comparing(ReservationsEntity::getReservationDate).reversed());

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
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
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
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
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
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
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
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "error: 로그인 후 이용 가능합니다."; // 비로그인 사용자 처리
        }

        commentService.deleteComment(commentId);
        return "success";
    }

    // 더보기 댓글 로드
    @GetMapping("/showDetail/loadComments")
    @ResponseBody
    public Map<String, Object> loadMoreComments(
            @RequestParam("performanceId") int performanceId,
            @RequestParam("offset") int offset,
            Principal principal) {

        PerformancesEntity performance = new PerformancesEntity();
        performance.setId(performanceId);
        List<CommentEntity> comments = commentService.getComments(performance, offset, 5);

        // 현재 사용자 정보 가져오기
        int userId = 0;
        boolean isAdmin = false;
        if (principal != null) {
            String loginId = principal.getName();
            UserEntity user = userService.getUserByLoginId(loginId);
            userId = user.getId();
            isAdmin = user.getRole() == UserEntity.Role.ADMIN; // 관리자 여부 확인
        }

        Map<String, Object> response = new HashMap<>();
        response.put("comments", comments);
        response.put("userId", userId);
        response.put("isAdmin", isAdmin);

        return response;
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
    public String showSeatSelection(@RequestParam("showId") int showId, Model model) {
        // showId에 해당하는 예약된 좌석을 조회
        List<String> unavailableSeats = reservationService.getUnavailableSeats(showId);
        
        // unavailableSeats 리스트를 모델에 담아 JSP나 Thymeleaf 템플릿으로 전달
        model.addAttribute("unavailableSeats", unavailableSeats);
        
        // showId를 폼에 hidden으로 전달할 수 있도록
        model.addAttribute("showId", showId);
        
        return "seatSelect";
    }

    @PostMapping("/reservConfirm")
    public String showReservConfirmPage(
        @RequestParam("performanceId") String performanceId,
        @RequestParam("performanceTitle") String performanceTitle,
        @RequestParam("showId") String showId,
        @RequestParam("showDate") String showDate,
        @RequestParam("showTime") String showTime,
        @RequestParam("numberOfPeople") int numberOfPeople,
        @RequestParam("seatId1") String seatId1,
        @RequestParam("seatId2") String seatId2,
        Model model) {
        
        // 모델에 값 저장
        model.addAttribute("performanceId", performanceId);
        model.addAttribute("performanceTitle", performanceTitle);
        model.addAttribute("showId", showId);
        model.addAttribute("showDate", showDate);
        model.addAttribute("showTime", showTime);
        model.addAttribute("numberOfPeople", numberOfPeople);
        model.addAttribute("seatId1", seatId1);
        model.addAttribute("seatId2", seatId2);
        
        // 예약 확인 페이지로 이동
        return "reservConfirm";
    }

    @RequestMapping(value = "/reservForm", method = {RequestMethod.GET, RequestMethod.POST})
    public String showReservationForm(@RequestParam("performanceId") int performanceId,
                                    Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인되지 않은 경우
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("alertMessage", "로그인 후 이용하세요.");
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        // performanceId 사용하여 공연 정보 조회
        Optional<PerformancesEntity> performance = performanceService.getPerformanceById(performanceId);

        if (performance.isPresent()) { // Optional에서 값 꺼내기
            model.addAttribute("performance", performance.get()); // 공연 정보를 모델에 추가
        } else {
            // 공연 정보가 없으면 오류 메시지 처리
            model.addAttribute("errorMessage", "해당 공연 정보를 찾을 수 없습니다.");
        }

        // 오늘 날짜를 Calendar 객체로 가져오기
        Calendar calendar = Calendar.getInstance();
        Date today = (Date) calendar.getTime();
        
        // 오늘 날짜를 문자열로 변환하여 모델에 추가
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = dateFormat.format(today);
        
        model.addAttribute("today", todayString); // 오늘 날짜를 모델에 추가
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
