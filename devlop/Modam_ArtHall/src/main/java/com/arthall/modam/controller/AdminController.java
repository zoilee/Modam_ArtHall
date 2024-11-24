package com.arthall.modam.controller;

import com.arthall.modam.entity.NoticesEntity;
import com.arthall.modam.dto.PerformancesDto;
import com.arthall.modam.entity.ImagesEntity;
import com.arthall.modam.service.BbsService;
import com.arthall.modam.service.FileService;
import com.arthall.modam.service.PerformanceService;
import com.arthall.modam.service.UserService;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.repository.ImagesRepository;
import com.arthall.modam.repository.PerformancesRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PerformancesRepository performancesRepository;

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private BbsService bbsService;

    @Autowired
    private FileService fileService;

     @Autowired
    private UserService userService;

    @Autowired
    private PerformanceService performanceService;

    

    // 공지사항 목록 조회 (페이지네이션 적용)
    @GetMapping("/noticeList")
    public String showAdminNoticeList(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 5; // 페이지당 표시할 공지사항 수
        Page<NoticesEntity> notices = bbsService.getNotices(page, pageSize);
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
        NoticesEntity notice = new NoticesEntity();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setAdminId(1); // 예시 관리자 ID 설정

        // 공지사항을 먼저 데이터베이스에 저장
        NoticesEntity savedNotice = bbsService.saveNotice(notice);

        if (file != null && !file.isEmpty()) {
            // 파일 저장 로직
            try {
                String filePath = fileService.saveFile(file); // 파일 저장 후 경로 반환

                // images 테이블에 데이터 저장
                ImagesEntity image = new ImagesEntity();
                image.setImageUrl(filePath);
                image.setReferenceId(savedNotice.getId()); // 저장된 공지사항의 ID 사용
                image.setReferenceType(ImagesEntity.ReferenceType.NOTICE); // 공지사항임을 표시
                imagesRepository.save(image);

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
    public String showEditForm(@RequestParam("id") int id, Model model) {
        NoticesEntity notice = bbsService.getNoticeById(id).orElse(null);
        System.out.println("노티스 정보" + notice);
        model.addAttribute("notice", notice);
        return "admin/adminNoticeEdit";
    }

    // 수정 요청 처리
    @PostMapping("/noticeEdit")
public String updateAdminNotice(
        @RequestParam("id") int id,
        @RequestParam("title") String title,
        @RequestParam("content") String content,
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam(value = "deleteImageIds", required = false) List<Integer> deleteImageIds,
        RedirectAttributes redirectAttributes) {

    try {
        // 트랜잭션 시작
        // 공지사항 엔티티 조회
        NoticesEntity notice = bbsService.getNoticeById(id).orElseThrow(
            () -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id)
        );

        // 공지사항 정보 수정
        notice.setTitle(title);
        notice.setContent(content);

        // 이미지 삭제 처리 (트랜잭션 내부에서 실행)
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            List<ImagesEntity> imagesToDelete = imagesRepository.findAllById(deleteImageIds);
            for (ImagesEntity image : imagesToDelete) {
                try {
                    // 파일 삭제
                    fileService.deleteFile(image.getImageUrl());
                    imagesRepository.delete(image); // 데이터베이스에서 삭제
                } catch (IOException e) {
                    System.err.println("이미지 삭제 중 오류 발생: " + image.getImageUrl());
                    throw new RuntimeException("이미지 삭제 중 오류가 발생했습니다.");
                }
            }
        }

        // 새 이미지 업로드 처리
        if (file != null && !file.isEmpty()) {
            String filePath = fileService.saveFile(file); // 파일 저장
            ImagesEntity newImage = new ImagesEntity();
            newImage.setImageUrl(filePath);
            newImage.setReferenceId(id);
            newImage.setReferenceType(ImagesEntity.ReferenceType.NOTICE);
            imagesRepository.save(newImage);
        }

        // 공지사항 저장
        bbsService.saveNotice(notice);

        // 성공 메시지 설정
        redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 수정되었습니다.");
        return "redirect:/admin/noticeList";

    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "공지사항 수정 중 오류가 발생했습니다.");
        e.printStackTrace();
    }

    return "redirect:/admin/noticeEdit?id=" + id; // 오류 발생 시 다시 수정 페이지로 이동
}

    
    

    // 삭제 요청 처리
    @PostMapping("/noticeDelete")
    public String deleteAdminNotice(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        NoticesEntity notice = bbsService.getNoticeById(id).orElse(null);

        if (notice != null) {
            // 공지사항에 연결된 이미지 가져오기
            List<ImagesEntity> images = imagesRepository.findByReferenceIdAndReferenceType(id,
                    ImagesEntity.ReferenceType.NOTICE);

            // 이미지 파일 삭제
            boolean fileDeleteError = false;
            for (ImagesEntity image : images) {
                try {
                    fileService.deleteFile(image.getImageUrl());
                } catch (IOException e) {
                    System.err.println("이미지 파일 삭제 실패: " + image.getImageUrl());
                    fileDeleteError = true;
                }
            }

            // 이미지 데이터 삭제
            imagesRepository.deleteAll(images);

            // 공지사항 삭제
            bbsService.deleteNotice(id);

            if (fileDeleteError) {
                redirectAttributes.addFlashAttribute("message", "이미지 파일 삭제 중 일부 오류가 발생했습니다.");
            } else {
                redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 삭제되었습니다.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "공지사항을 찾을 수 없습니다.");
        }

        return "redirect:/admin/noticeList";
    }

    // 공지사항 상세 조회 페이지
    @GetMapping("/noticeView")
    public String showAdminNoticeView(@RequestParam("id") int id, Model model) {
        NoticesEntity notice = bbsService.getNoticeById(id).orElse(null);

        System.out.println("Upload directory absolute path: " + new File("./uploads/").getAbsolutePath());
        System.out.println("노티스 정보" + notice);
        model.addAttribute("notice", notice);
        return "admin/adminNoticeView"; // 뷰 이름을 adminNoticeView로 설정
    }

    //////////////////////////////////////////////////////////////////////
    @GetMapping("/menu")
    public String showAdminMenu() {
        return "admin/adminMenu";
    }

    @GetMapping("/redservView")
    public String reservationStatus(Model model) {
        
        List<PerformancesDto> performances = performanceService.getPerformancesWithReservationRate();
        model.addAttribute("performances", performances);
        return "admin/adminRedservView";
    }
    

    @GetMapping("/showCommitList")
    public String showAdminCommitList(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 5; // 페이지당 표시할 공지사항 수

        Page<PerformancesEntity> performances = bbsService.getPerformances(page, pageSize);

        model.addAttribute("performances", performances);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", performances.getTotalPages()); // 전체 페이지 수 전달

        return "admin/adminShowCommitList";
    }

    @GetMapping("/showCommitWrite")
    public String showAdminCommitWrite() {

        return "admin/adminShowCommitWrite";
    }

    @PostMapping("/showComitWrite")
    public String AdminCommitWrite(PerformancesEntity performancesEntity,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {

        // 공연데이터가 먼저 생성
        PerformancesEntity savedPerformance = performancesRepository.save(performancesEntity);

        if (file != null && !file.isEmpty()) {
            // 파일 저장 로직
            try {
                String filePath = fileService.saveFile(file); // 파일 저장 후 경로 반환
                System.out.println(filePath);
                // images 테이블에 데이터 저장
                ImagesEntity image = new ImagesEntity();
                image.setImageUrl(filePath);
                image.setReferenceId(savedPerformance.getId()); // 저장된 공연정보의 ID 사용
                image.setReferenceType(ImagesEntity.ReferenceType.PERFORMANCE); // 공연정보로 등록
                imagesRepository.save(image);

            } catch (IOException e) {
                System.err.println("파일 저장 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }

        redirectAttributes.addFlashAttribute("message", "공연 정보가 성공적으로 등록되었습니다.");

        return "redirect:showCommitList";
    }

    @GetMapping("/showCommitDelete")
    public String showCommitDelete(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {

        // 공연데이터 가져오기
        PerformancesEntity performance = performancesRepository.findById(id).orElse(null);
        ImagesEntity.ReferenceType referenceType = ImagesEntity.ReferenceType.PERFORMANCE;

        if (performance == null) {
            redirectAttributes.addFlashAttribute("error", "공연 정보를 찾을 수 없습니다.");
            return "redirect:showCommitList";
        }

        List<ImagesEntity> images = imagesRepository.findByReferenceIdAndReferenceType(performance.getId(),
                referenceType);

        // 파일 삭제
        boolean filDeleteError = false;
        for (ImagesEntity image : images) {
            try {
                fileService.deleteFile(image.getImageUrl());
                System.out.println("파일 삭제 성공: " + image.getImageUrl());
            } catch (IOException e) {
                System.err.println("파일 삭제 실패: " + image.getImageUrl());
                filDeleteError = true;
            }

        }

        // 이미지 db 삭제
        imagesRepository.deleteAll(images);

        // 공연 db 삭제
        performancesRepository.deleteById(id);

        if (filDeleteError) {
            redirectAttributes.addFlashAttribute("message", "이미지파일이 삭제되지 않았습니다.");
        } else {
            redirectAttributes.addFlashAttribute("message", "공연이 삭제되었습니다.");
        }

        return "redirect:showCommitList";
    }

    @GetMapping("/showCommitView")
    public String showCommitView(@RequestParam("id") int id, Model model) {
        PerformancesEntity performance = bbsService.getPerformancesById(id).orElse(null);
        System.out.println(performance);
        model.addAttribute("performance", performance);
        return "admin/adminShowCommitView";
    }

    @GetMapping("/showCommitEdit")
    public String showAdminCommitWrite(@RequestParam("id") int id, Model model) {
        PerformancesEntity performance = performancesRepository.findById(id).orElse(null);
        model.addAttribute("performance", performance);
        return "admin/adminShowCommitEdit";
    }

    @SuppressWarnings("null")
    @PostMapping("/showCommitEdit")
    public String adminCommitEdit(PerformancesEntity performancesEntity,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "deleteImage", required = false) boolean deleteImage,
            RedirectAttributes redirectAttributes) {

        try {
            // 기존 엔티티 조회
            PerformancesEntity existingEntity = performancesRepository.findById(performancesEntity.getId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 공연 정보를 찾을 수 없습니다."));

            existingEntity.setTitle(performancesEntity.getTitle());
            existingEntity.setStart_date(performancesEntity.getStart_date());
            existingEntity.setEnd_date(performancesEntity.getEnd_date());
            existingEntity.setLocation(performancesEntity.getLocation());
            existingEntity.setTime(performancesEntity.getTime());
            existingEntity.setAge(performancesEntity.getAge());

            // 공연 정보 저장
            performancesRepository.save(existingEntity);

            System.out.println("기존 공연 ID: " + existingEntity.getId());

            ImagesEntity.ReferenceType referenceType = ImagesEntity.ReferenceType.PERFORMANCE;

            // 이미지 삭제 처리
            if (deleteImage) {
                List<ImagesEntity> images = imagesRepository
                        .findByReferenceIdAndReferenceType(performancesEntity.getId(), referenceType);
                if (images != null && !images.isEmpty()) {
                    for (ImagesEntity image : images) {
                        try {
                            // 파일 삭제 수행
                            fileService.deleteFile(image.getImageUrl());
                            System.out.println("파일 삭제 성공: " + image.getImageUrl());
                        } catch (Exception e) {
                            // 예외 발생 시 로그 출력
                            System.err.println("파일 삭제 중 예외 발생: " + image.getImageUrl());
                            e.printStackTrace();
                        }
                    }
                }
                imagesRepository.deleteAll(images);
            }

            // 파일 업로드 처리
            if (file != null && !file.isEmpty()) {
                String filePath = fileService.saveFile(file);
                ImagesEntity image = new ImagesEntity();
                image.setImageUrl(filePath);
                image.setReferenceId(existingEntity.getId());
                image.setReferenceType(referenceType);

                System.out.println("이미지에 설정된 Reference ID: " + image.getReferenceId());
                imagesRepository.save(image);
            }

            redirectAttributes.addFlashAttribute("message", "공연 정보가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "공연 정보 업데이트 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return "redirect:showCommitList";
    }



    @GetMapping("/userCommit")
public String showAdminUserCommit(
        @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
        @RequestParam(value = "page", defaultValue = "0") int page,
        Model model) {

    System.out.println("검색 키워드: " + keyword); // 요청받은 키워드 출력

    int pageSize = 5; // 페이지당 회원 수
    Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());

    Page<UserEntity> users = userService.searchUsers(keyword, pageable);

    System.out.println("검색 결과 수: " + users.getContent().size()); // 검색 결과 수 출력
    model.addAttribute("users", users);
    model.addAttribute("keyword", keyword);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", users.getTotalPages());

    return "admin/adminUserCommit";
}

    
    
@GetMapping("/users/edit")
public String editUserForm(@RequestParam("id") int userId, Model model) {
    // 데이터베이스에서 사용자 정보를 가져오기
    UserEntity user = userService.getUserById(userId);

    // 가져온 사용자 데이터를 모델에 추가
    model.addAttribute("user", user);

    // "adminUserEditForm" 템플릿으로 이동
    return "admin/adminUserEditForm";
}

@PostMapping("/users/update")
public String updateUser(
        @RequestParam("id") int userId,
        @RequestParam("loginId") String loginId,
        @RequestParam("name") String name,
        @RequestParam("email") String email,
        @RequestParam("phoneNumber") String phoneNumber,
        @RequestParam("role") String role,
        RedirectAttributes redirectAttributes) {

    userService.updateUser(userId, loginId, name, email, phoneNumber, role);
    redirectAttributes.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
    return "redirect:/admin/userCommit"; // 수정 후 회원 목록 페이지로 이동
}





    



}
