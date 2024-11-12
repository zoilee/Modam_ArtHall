package com.arthall.modam.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.repository.PerformancesRepository;
import com.arthall.modam.dto.PerformancesDto;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PerformancesRepository performancesRepository;

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
    public String showAdminCommitList(
            Model model) {
        int page = 0;
        int size = 10;
        Sort sort = Sort.by("id").descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PerformancesEntity> PerforPage = performancesRepository.findAll(pageable);

        Page<PerformancesDto> dtoPage = PerforPage.map(PerformancesDto::toPerformancesDto);

        model.addAttribute("performances", dtoPage);

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
