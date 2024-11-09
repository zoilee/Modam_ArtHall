package com.arthall.modam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/menu")
    public String showAdminMenu() {
        return "admin/adminMenu";
    }

    @GetMapping("/noticeList")
    public String showAdminNoticeList() {
        return "admin/adminNoticeList";
    }

    @GetMapping("/noticeView")
    public String showAdminNoticeView() {
        return "admin/adminNoticeView";
    }

    @GetMapping("/noticeWrite")
    public String showAdminNoticeWrite() {
        return "admin/adminNoticeWrite";
    }

    @GetMapping("/redservView")
    public String showAdminRedservView() {
        return "admin/adminRedservView";
    }

    @GetMapping("/showCommitList")
    public String showAdminCommitList() {
        return "admin/adminShowCommitList";
    }

    @GetMapping("/showCommitWrite")
    public String showAdminCommitWrite() {
        return "admin/adminShowCommitWrite";
    }

    @GetMapping("/userCommit")
    public String showAdminUserCommit() {
        return "admin/adminUserCommit";
    }
}
