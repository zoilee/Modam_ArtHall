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
            AdminNoticeList notice = new AdminNoticeList();
            notice.setTitle(title);
            notice.setContent(content);
            notice.setAdminId(1L); // 예시 관리자 ID 설정

            if (file != null && !file.isEmpty()) {
                // 파일 저장 로직
                String filePath = fileService.saveFile(file); // 파일 저장 후 경로 반환

                // images 테이블에 데이터 저장
                Image image = new Image();
                image.setImageUrl(filePath);
                image.setReferenceId(notice.getId());
                image.setReferenceType("notice"); // 공지사항임을 표시
                imageRepository.save(image);
                
                // 공지사항에 이미지 정보 설정 (이미지 URL 사용 시)
                notice.setImageUrl(filePath);
            }

            adminNoticeListService.saveNotice(notice);
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 등록되었습니다.");
            return "redirect:/admin/noticeList";
        }



       // 공지사항 상세 조회
        @GetMapping("/noticeView")
        public String showAdminNoticeView(@RequestParam("id") Long id, Model model) {
            // 공지사항 ID에 해당하는 데이터를 조회
            AdminNoticeList notice = adminNoticeListService.getNoticeById(id).orElse(null);

            // 조회한 공지사항을 모델에 추가하여 뷰로 전달
            model.addAttribute("notice", notice);
            return "admin/adminNoticeView"; // 공지사항 상세 페이지
        }

        //수정
        @GetMapping("/noticeEdit")
        public String showEditForm(@RequestParam("id") Long id, Model model) {
            AdminNoticeList notice = adminNoticeListService.getNoticeById(id).orElse(null);
            model.addAttribute("notice", notice);
            return "admin/adminNoticeEdit";
        }



        // 수정 요청 처리
        @PostMapping("/noticeEdit")
    public String updateAdminNotice(@RequestParam("id") Long id,
                                    @RequestParam("title") String title,
                                    @RequestParam("content") String content,
                                    @RequestParam(value = "file", required = false) MultipartFile file,
                                    RedirectAttributes redirectAttributes) {
        AdminNoticeList notice = adminNoticeListService.getNoticeById(id).orElse(null);
        if (notice != null) {
            notice.setTitle(title);
            notice.setContent(content);

            if (file != null && !file.isEmpty()) {
                // 새 이미지 파일 저장 로직
                String filePath = fileService.saveFile(file);

                // 기존 이미지가 있다면 삭제
                if (notice.getImageUrl() != null) {
                    fileService.deleteFile(notice.getImageUrl());
                }

                // 새 이미지 정보 저장
                Image image = new Image();
                image.setImageUrl(filePath);
                image.setReferenceId(notice.getId());
                image.setReferenceType("notice");
                imageRepository.save(image);

                // 공지사항에 새 이미지 URL 설정
                notice.setImageUrl(filePath);
            }

            adminNoticeListService.saveNotice(notice);
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 수정되었습니다.");
        }
        return "redirect:/admin/noticeList";
    }


        // 삭제 요청 처리
        @PostMapping("/noticeDelete")
        public String deleteAdminNotice(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
            adminNoticeListService.deleteNotice(id);
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 삭제되었습니다.");
            return "redirect:/admin/noticeList";
        }




        //////////////////////////////////////////////////////////////////////
        @GetMapping("/menu")
        public String showAdminMenu() {
            return "admin/adminMenu";
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
