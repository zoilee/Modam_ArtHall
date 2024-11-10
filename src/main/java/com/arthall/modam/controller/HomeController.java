package com.arthall.modam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
@GetMapping("/")
    public String home() {
        // resources/templates/ + 뷰이름 + .html
        return "main"; 
    }

@GetMapping("/login")
    public String login(){
        return "login";
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

@GetMapping("/reservConfirm")
    public String reservConfirm(){
        return "reservConfirm";
    }
    
@GetMapping("/seatSelect")
    public String seatSelect(){
        return "seatSelect";
    }
}
