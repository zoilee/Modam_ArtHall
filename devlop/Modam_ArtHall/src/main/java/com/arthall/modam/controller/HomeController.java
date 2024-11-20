package com.arthall.modam.controller;

import java.util.List;
import java.security.Principal;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arthall.modam.entity.CommentEntity;
import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.service.CommentService;
import com.arthall.modam.service.PerformanceService;
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
    private CommentService commentService;

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
            // 사용자 역할 가져오기
            String userRole = getUserRole(authentication);
            model.addAttribute("userRole", userRole); // 역할 정보를 Model에 추가
        } else {
            model.addAttribute("userRole", "ROLE_ANONYMOUS"); // 비로그인 사용자
        }

        return "main";
    }

    @GetMapping("/registeruserEdit")
    public String registeruserEdit() {
        return "registeruserEdit";
    }

    @GetMapping("/showDetail/{performanceId}")
    public String showPerformanceDetails(@PathVariable("performanceId") int performanceId, Model model,
            Principal principal) {
        // 평균 평점이 설정된 PerformanceEntity 가져오기
        PerformancesEntity performance = performanceService.getPerformanceWithAverageRating(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("공연을 찾을 수 없습니다."));

        UserEntity user = null;
        boolean isAdmin = false; // 관리자 여부 확인

        if (principal != null) {
            String loginId = principal.getName(); // 로그인된 사용자의 loginId 가져오기
            user = userService.getUserByLoginId(loginId); // loginId로 사용자 조회

            // 관리자 여부 확인
            isAdmin = userService.isAdmin(user); // 사용자 서비스에서 ROLE_ADMIN 확인

            model.addAttribute("userId", user.getId()); // 로그인된 사용자 ID 추가
            model.addAttribute("isAdmin", isAdmin); // 관리자 여부 추가
        } else {
            model.addAttribute("userId", 0); // 비로그인 사용자는 ID를 0으로 설정
            model.addAttribute("isAdmin", false); // 비로그인 사용자는 관리자 아님
        }

        // PerformanceEntity 및 CommentEntity 가져오기
        List<CommentEntity> comments = commentService.getComments(performance, 0, 5);

        // 총 댓글 수 계산
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
            @RequestParam("userId") int userId,
            @RequestParam("performanceId") int performanceId,
            @RequestParam("commentText") String commentText,
            @RequestParam("rating") int rating) {
        UserEntity user = new UserEntity();
        user.setId(userId);

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
        commentService.updateComment(commentId, commentText);
        return "success";
    }

    @PostMapping("/showDetail/deleteComment")
    @ResponseBody
    public String deleteComment(@RequestParam("commentId") int commentId) {
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
