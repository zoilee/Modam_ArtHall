package com.arthall.modam.controller;

import com.arthall.modam.entity.AdminNoticeList;
import com.arthall.modam.entity.Image;
import com.arthall.modam.service.AdminNoticeListService;
import com.arthall.modam.service.FileService;
import com.arthall.modam.repository.ImageRepository;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.arthall.modam.entity.ImagesEntity;
import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.entity.TemporaryImagesEntity;
import com.arthall.modam.repository.ImagesRepository;
import com.arthall.modam.repository.PerformancesRepository;
import com.arthall.modam.repository.TemporaryImagesRepository;
import com.arthall.modam.dto.PerformancesDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PerformancesRepository performancesRepository;

    @Autowired
    private TemporaryImagesRepository temporaryImagesRepository;

    @Autowired
    private ImagesRepository imagesRepository;

    @Value("${custom.upload-dir}")
    private String uploadDir;

    @Value("${custom.temp-dir}")
    private String tempDir;

    @GetMapping("/menu")
    public String showAdminMenu() {
        return "admin/adminMenu";
    }

    @Autowired
    private AdminNoticeListService adminNoticeListService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ImageRepository imageRepository;

    // 공지사항 목록 조회 (페이지네이션 적용)
    @GetMapping("/noticeList")
    public String showAdminNoticeList(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 5; // 페이지당 표시할 공지사항 수
        Page<AdminNoticeList> notices = adminNoticeListService.getNotices(page, pageSize);
        model.addAttribute("notices", notices); // Page 객체 자체를 모델에 추가
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notices.getTotalPages()); // 전체 페이지 수 전달
        return "admin/adminNoticeList";
    }

    // 공지사항 작성 페이지
    @GetMapping("/noticeWrite")
    public String showAdminNoticeWrite() {
        return "admin/adminNoticeWrite";
    }

    // 공지사항 저장
    @PostMapping("/noticeWrite")
    public String saveAdminNotice(@RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {
        AdminNoticeList notice = new AdminNoticeList();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setAdminId(1L); // 예시 관리자 ID 설정

        // 공지사항을 먼저 데이터베이스에 저장
        AdminNoticeList savedNotice = adminNoticeListService.saveNotice(notice);

        if (file != null && !file.isEmpty()) {
            // 파일 저장 로직
            try {
                String filePath = fileService.saveFile(file); // 파일 저장 후 경로 반환

                // images 테이블에 데이터 저장
                Image image = new Image();
                image.setImageUrl(filePath);
                image.setReferenceId(savedNotice.getId().intValue()); // 저장된 공지사항의 ID 사용
                image.setReferenceType("notice"); // 공지사항임을 표시
                imageRepository.save(image);

                // 공지사항에 이미지 정보 설정 (이미지 URL 사용 시)
                savedNotice.setImageUrl(filePath);
                adminNoticeListService.saveNotice(savedNotice); // 이미지 경로 업데이트 후 다시 저장
            } catch (IOException e) {
                System.err.println("파일 저장 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }

        redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 등록되었습니다.");
        return "redirect:/admin/noticeList";
    }

    // 수정 페이지
    @GetMapping("/noticeEdit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        AdminNoticeList notice = adminNoticeListService.getNoticeById(id).orElse(null);
        model.addAttribute("notice", notice);
        return "admin/adminNoticeEdit";
    }

    // 수정 요청 처리
    // 수정 요청 처리
    @PostMapping("/noticeEdit")
    public String updateAdminNotice(@RequestParam("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "deleteImage", required = false) boolean deleteImage,
            RedirectAttributes redirectAttributes) {
        AdminNoticeList notice = adminNoticeListService.getNoticeById(id).orElse(null);
        if (notice != null) {
            notice.setTitle(title);
            notice.setContent(content);

            // 이미지 삭제 요청 처리
            if (deleteImage && notice.getImageUrl() != null) {
                try {
                    fileService.deleteFile(notice.getImageUrl());
                    notice.setImageUrl(null); // 기존 이미지 경로 제거
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 새 파일이 업로드된 경우 처리
            if (file != null && !file.isEmpty()) {
                try {
                    String filePath = fileService.saveFile(file);
                    notice.setImageUrl(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            adminNoticeListService.saveNotice(notice);
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 수정되었습니다.");
        }
        return "redirect:/admin/noticeList";
    }

    // 삭제 요청 처리
    @PostMapping("/noticeDelete")
    public String deleteAdminNotice(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        AdminNoticeList notice = adminNoticeListService.getNoticeById(id).orElse(null);

        if (notice != null) {
            if (notice.getImageUrl() != null) {
                try {
                    fileService.deleteFile(notice.getImageUrl());
                } catch (IOException e) {
                    System.err.println("이미지 파일 삭제 실패: " + notice.getImageUrl());
                    e.printStackTrace();
                }
            }
            adminNoticeListService.deleteNotice(id);
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 삭제되었습니다.");
        }

        return "redirect:/admin/noticeList";
    }

    // 공지사항 상세 조회 페이지
    @GetMapping("/noticeView")
    public String showAdminNoticeView(@RequestParam("id") Long id, Model model) {
        AdminNoticeList notice = adminNoticeListService.getNoticeById(id).orElse(null);
        model.addAttribute("notice", notice);
        return "admin/adminNoticeView"; // 뷰 이름을 adminNoticeView로 설정
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

    @PostMapping("/showComitWrite")
    public String AdminCommitWrite(PerformancesEntity performancesEntity,
            @RequestParam("imageTokens") List<String> imageTokens) {

        // 임시 이미지 가져오기
        List<TemporaryImagesEntity> images = temporaryImagesRepository.findByReferenceTokenIn(imageTokens);

        // 공연데이터가 먼저 생성
        performancesRepository.save(performancesEntity);

        ImagesEntity.ReferenceType referenceType = ImagesEntity.ReferenceType.PERFORMANCE;

        for (TemporaryImagesEntity tempImage : images) {
            try {
                String uniqueFileName = tempImage.getImageUrl().substring(tempImage.getImageUrl().lastIndexOf("/") + 1);
                Path sourcePath = Paths.get(tempDir, uniqueFileName);
                Path targetPath = Paths.get(uploadDir, uniqueFileName);

                if (!Files.exists(targetPath.getParent())) {
                    Files.createDirectories(targetPath.getParent());
                    System.out.println("Target 디렉토리가 생성되었습니다: " + targetPath.getParent());
                }

                if (Files.exists(sourcePath)) {
                    Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("파일이 이동되었습니다: " + sourcePath + " -> " + targetPath);
                } else {
                    System.out.println("소스 파일이 존재하지 않습니다: " + sourcePath);
                }

                ImagesEntity image = new ImagesEntity();
                image.setImageUrl("/uploads/" + uniqueFileName);
                image.setReferenceType(referenceType);
                image.setReferenceId(performancesEntity.getId()); // 공연 id 설정

                imagesRepository.save(image); // images 테이블에 저장
                System.out.println("이미지가 저장됬습니다");

                temporaryImagesRepository.delete(tempImage);
                System.out.println("임시 파일이 삭제되었습니다");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        return "redirect:showCommitList";
    }

    @GetMapping("/showCommitDelete")
    public String showCommitDelete(@RequestParam("id") int performanceId) {

        // 공연데이터 가져오기
        PerformancesEntity performance = performancesRepository.findById(performanceId).orElse(null);
        ImagesEntity.ReferenceType referenceType = ImagesEntity.ReferenceType.PERFORMANCE;
        // 이미지데이터 가져오기
        List<ImagesEntity> images = imagesRepository.findByReferenceTypeAndReferenceId(
                referenceType, performanceId);

        // 이미지 파일 삭제
        for (ImagesEntity image : images) {
            try {
                String imageUrl = image.getImageUrl();
                String uniqueFileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(uploadDir, uniqueFileName);
                // 파일이 존재하는 경우 삭제
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    System.out.println("파일이 삭제되었습니다: " + filePath);
                } else {
                    System.out.println("파일이 존재하지 않아 삭제할 수 없습니다: " + filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 이미지 db 삭제
        imagesRepository.deleteAll(images);

        // 공연 db 삭제
        performancesRepository.delete(performance);

        return "redirect:showCommitList";
    }

    @GetMapping("/showCommitView")
    public String showCommitView(@RequestParam("id") int performanceId) {

        return "admin/adminShowCommitWrite";
    }

    @GetMapping("/userCommit")
    public String showAdminUserCommit() {
        return "admin/adminUserCommit";
    }
}
