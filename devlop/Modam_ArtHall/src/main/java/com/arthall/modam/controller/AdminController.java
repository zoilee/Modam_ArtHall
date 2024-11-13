    package com.arthall.modam.controller;

    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.multipart.MultipartFile;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.arthall.modam.entity.AdminNoticeList;
import com.arthall.modam.service.AdminNoticeListService;

    @Controller
    @RequestMapping("/admin")
    public class AdminController {

        @Autowired
        private AdminNoticeListService adminNoticeListService;

        // 공지사항 목록 조회 (페이지네이션 적용)
        @GetMapping("/noticeList")
        public String showAdminNoticeList(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
            int pageSize = 5; // 페이지당 표시할 공지사항 수
            Page<AdminNoticeList> notices = adminNoticeListService.getNotices(page, pageSize);
            model.addAttribute("notices", notices);  // Page 객체 자체를 모델에 추가
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", notices.getTotalPages());  // 전체 페이지 수 전달
            return "admin/adminNoticeList";
        }



        // 공지사항 작성 페이지
        @GetMapping("/noticeWrite")
        public String showAdminNoticeWrite() {
            return "admin/adminNoticeWrite";
        }

        @PostMapping("/noticeWrite")
        public String saveAdminNotice(@RequestParam("title") String title,
                                    @RequestParam("content") String content,
                                    @RequestParam(value = "file", required = false) MultipartFile file,
                                    RedirectAttributes redirectAttributes) {
            // 새로운 공지사항 객체 생성
            AdminNoticeList notice = new AdminNoticeList();
            notice.setTitle(title);
            notice.setContent(content);
            notice.setAdminId(1L); // 관리자 ID 예시로 설정 (실제 구현에서는 로그인한 관리자 ID를 사용)

            // 파일 처리 로직 (필요에 따라 구현)
            if (file != null && !file.isEmpty()) {
                // 파일 저장 로직을 구현하세요.
            }

            // 공지사항 저장
            adminNoticeListService.saveNotice(notice);

            // 성공 메시지와 함께 리다이렉트
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 등록되었습니다.");
            return "redirect:/admin/noticeList";
        }








        //////////////////////////////////////////////////////////////////////
        @GetMapping("/menu")
        public String showAdminMenu() {
            return "admin/adminMenu";
        }

        @GetMapping("/noticeView")
        public String showAdminNoticeView() {
            return "admin/adminNoticeView";
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
