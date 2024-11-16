package com.arthall.modam.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
        }

        return "main";
    }

@GetMapping("/mypage")
    public String mypage(){
        return "mypage";
    }

@GetMapping("/registeruserEdit")
    public String registeruserEdit(){
        return "registeruserEdit";
    }
    
@GetMapping("/showDetail")
    public String showDetail(){
        return "showDetail";
    }
    
@GetMapping("/hallDetail")
    public String hallDetail(){
        return "hallDetail";
    }

@GetMapping("/noticeList")
    public String noticeList(){
        return "noticeList";
    }

@GetMapping("/noticeView")
    public String noticeView(){
        return "noticeView";
    }

@GetMapping("/seatSelect")
    public String seatSelect(){
        return "seatSelect";
    }

@GetMapping("/reservConfirm")
    public String reservConfirm(){
        return "reservConfirm";
    }

@GetMapping("/reservForm")
    public String reservForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인되지 않은 경우
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("alertMessage", "로그인 후 이용하세요.");
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        // 로그인된 경우 예약 폼 페이지로 이동
        return "reservForm";
    }
}
