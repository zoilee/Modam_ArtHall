package com.arthall.modam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arthall.modam.entity.CommentEntity;
import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.entity.ReservationEntity;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.service.CommentService;
import com.arthall.modam.service.PerformanceService;
import com.arthall.modam.service.ReservationService;
import com.arthall.modam.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "main";
    }

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PerformanceService performanceService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/mypage")
    public String mypage(Model model) {
        int userId = 1; // 예시 사용자 ID, 실제로는 인증된 사용자 ID 사용
        UserEntity user = userService.getUserById(userId);
        int points = userService.getUserPoints(userId);

        List<ReservationEntity> upcomingReservations = reservationService.getUpcomingReservationsByUserId(userId);
        List<ReservationEntity> pastReservations = reservationService.getPastReservationsByUserId(userId);

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

        return "mypage"; // mypage.html로 이동
    }

    @GetMapping("/registeruserEdit")
    public String registeruserEdit() {
        return "registeruserEdit";
    }

    public HomeController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/showDetail/{performanceId}")
    public String showPerformanceDetails(@PathVariable("performanceId") int performanceId, Model model) {
        // 평균 평점이 설정된 PerformanceEntity 가져오기
        PerformancesEntity performance = performanceService.getPerformanceWithAverageRating(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("공연을 찾을 수 없습니다."));

        // 로그인된 사용자를 임시로 설정
        UserEntity user = new UserEntity();
        user.setId(1); // 로그인된 사용자의 ID를 1로 가정

        // PerformanceEntity 및 CommentEntity 가져오기
        List<CommentEntity> comments = commentService.getComments(performance, 0, 5);

        // 총 댓글 수 계산
        int totalComments = commentService.getTotalCommentsByPerformanceId(performanceId);

        // 모델에 데이터 추가
        model.addAttribute("performance", performance);
        model.addAttribute("comments", comments);
        model.addAttribute("userId", user.getId());
        model.addAttribute("totalComments", totalComments);

        return "showDetail"; // Thymeleaf 템플릿 이름
    }

    // 댓글 추가
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

    // 댓글 수정
    @PostMapping("/showDetail/updateComment")
    @ResponseBody
    public String updateComment(
            @RequestParam("commentId") int commentId,
            @RequestParam("commentText") String commentText) {
        commentService.updateComment(commentId, commentText);
        return "success";
    }

    // 댓글 삭제
    @PostMapping("/showDetail/deleteComment")
    @ResponseBody
    public String deleteComment(@RequestParam("commentId") int commentId) {
        commentService.deleteComment(commentId);
        return "success";
    }

    // 더보기 댓글 로드
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
    public String reservForm() {
        return "reservForm";
    }
}
