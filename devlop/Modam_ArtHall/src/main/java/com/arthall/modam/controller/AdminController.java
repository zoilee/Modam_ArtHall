package com.arthall.modam.controller;

import com.arthall.modam.entity.NoticesEntity;
import com.arthall.modam.dto.PerformancesDto;
import com.arthall.modam.entity.AlramEntitiy;
import com.arthall.modam.entity.ImagesEntity;
import com.arthall.modam.service.AlramService;
import com.arthall.modam.service.BbsService;
import com.arthall.modam.service.FileService;
import com.arthall.modam.service.PerformanceService;
import com.arthall.modam.service.ReservationsService;
import com.arthall.modam.service.UserService;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.entity.ReservationsEntity;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.repository.ImagesRepository;
import com.arthall.modam.repository.PerformancesRepository;
import com.arthall.modam.repository.ReservationsRepository;
import com.arthall.modam.repository.ShowRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ReservationsService reservationsService;

    @Autowired
    private PerformancesRepository performancesRepository;

    @Autowired
    private PerformanceService performanceService;

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private BbsService bbsService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationsRepository reservationRepository;

    @Autowired
    private AlramService alramService;

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
                    () -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));

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
    public String showAdminMenu(Model model) {
        // List<ReservationsEntity> reservations =
        // reservationsService.getTodayPaidReservations();
        // model.addAttribute("reservations", reservations);

        // 알람 처리
        List<AlramEntitiy> alrams = alramService.findAlramByReaded(false);
        model.addAttribute("alrams", alrams);

        return "admin/adminMenu";
    }

    @PutMapping("/alarms/{id}/read")
    @ResponseBody
    public ResponseEntity<Void> markAlramAsRead(@PathVariable("id") int id) {
        System.out.println("경로로 들어왔습니당");
        boolean success = alramService.markAsRead(id); // 읽음 처리
        if (success) {
            return ResponseEntity.ok().build(); // 성공 시 200 응답
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 실패 시 404 응답
        }
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

        int pageSize = 5; // 페이지당 표시할 공연 수
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PerformancesDto> performancesPage = performanceService.getPerformances(pageable);

        // PerformancesEntity를 PerformancesDto로 변환하고, 예약 수 추가 설정
        List<PerformancesDto> performances = performancesPage.getContent().stream()
                .peek(dto -> {
                    int reservationCount = reservationRepository.countByPerformanceId(dto.getId());
                    dto.setReservationCount(reservationCount);
                })
                .collect(Collectors.toList());

        model.addAttribute("performances", performances);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", performancesPage.getTotalPages()); // 전체 페이지 수 전달

        return "admin/adminShowCommitList";
    }

    // 작성 페이지 호출
    @GetMapping("/showCommitWrite")
    public String showAdminCommitWrite() {
        return "admin/adminShowCommitWrite";
    }

    // date 형식 바꾸기 메서드
    public Date convertStringToDate(String dateStr) {
        try {
            LocalDate localDate = LocalDate.parse(dateStr);
            return Date.valueOf(localDate); // LocalDate -> java.sql.Date로 변환
        } catch (DateTimeParseException e) {
            return null; // 날짜 변환 실패시 null 반환
        }
    }

    @PostMapping("/showCommitWrite")
    public String AdminCommitWrite(@ModelAttribute PerformancesDto performanceDto,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            RedirectAttributes redirectAttributes) {

        // 1. startDate와 endDate를 String으로 받았으므로, 이를 java.sql.Date로 변환
        Date startDate = convertStringToDate(startDateStr); // 변환된 startDate
        Date endDate = convertStringToDate(endDateStr); // 변환된 endDate

        // 2. PerformancesEntity 생성 및 값 설정
        PerformancesEntity performance = new PerformancesEntity();
        performance.setTitle(performanceDto.getTitle());
        performance.setDescription(performanceDto.getDescription());
        performance.setStartDate(startDate); // 변환된 startDate 설정
        performance.setEndDate(endDate); // 변환된 endDate 설정
        performance.setTime(performanceDto.getTime());
        performance.setLocation(performanceDto.getLocation());
        performance.setAge(performanceDto.getAge());

        // 3. 공연 데이터 저장
        PerformancesEntity savedPerformance = performancesRepository.save(performance);

        // 4. show 등록
        try {
            performanceService.registerShowsWithPerformance(performance);
            redirectAttributes.addFlashAttribute("message", "공연 정보가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "공연 등록 중 오류가 발생했습니다.");
            return "redirect:/admin/showCommitWrite"; // 오류 발생 시 폼으로 리다이렉트
        }

        // 5. 파일 처리 (이미지 저장)
        if (file != null && !file.isEmpty()) {
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
                redirectAttributes.addFlashAttribute("error", "파일 저장 중 오류가 발생했습니다.");
                return "redirect:/admin/showCommitWrite"; // 오류 발생 시 폼으로 리다이렉트
            }
        }

        // 등록이 끝나면 공연 목록으로 리다이렉트
        return "redirect:/admin/showCommitList";
    }

    @GetMapping("/showCommitDelete")
    public String showCommitDelete(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        PerformancesEntity performance = performancesRepository.findById(id).orElse(null);
        if (performance == null) {
            redirectAttributes.addFlashAttribute("error", "공연 정보를 찾을 수 없습니다.");
            return "redirect:showCommitList";
        }

        int reservationCount = reservationRepository.countByPerformanceId(performance.getId());
        if (reservationCount > 0) {
            redirectAttributes.addFlashAttribute("error", "예약이 있는 공연은 삭제가 불가합니다.");
            return "redirect:showCommitList";
        }

        List<ImagesEntity> images = imagesRepository.findByReferenceIdAndReferenceType(performance.getId(),
                ImagesEntity.ReferenceType.PERFORMANCE);
        boolean fileDeleteError = false;

        for (ImagesEntity image : images) {
            try {
                fileService.deleteFile(image.getImageUrl());
            } catch (IOException e) {
                System.err.println("파일 삭제 실패: " + image.getImageUrl());
                fileDeleteError = true;
            }
        }

        imagesRepository.deleteAll(images);
        performancesRepository.deleteById(id);

        if (fileDeleteError) {
            redirectAttributes.addFlashAttribute("message", "이미지 파일 삭제 중 일부 오류가 발생했습니다.");
        } else {
            redirectAttributes.addFlashAttribute("message", "공연이 삭제되었습니다.");
        }

        return "redirect:showCommitList";
    }

    @GetMapping("/showCommitView")
    public String showCommitView(@RequestParam("id") int id, Model model) {
        PerformancesEntity performance = bbsService.getPerformancesById(id).orElse(null);
        model.addAttribute("performance", performance);
        return "admin/adminShowCommitView";
    }

    @GetMapping("/showCommitEdit")
    public String showAdminCommitWrite(@RequestParam("id") int id, Model model) {
        PerformancesEntity performance = performancesRepository.findById(id).orElse(null);
        model.addAttribute("performance", performance);
        return "admin/adminShowCommitEdit";
    }

    @PostMapping("/showCommitEdit")
    public String adminCommitEdit(PerformancesEntity performancesEntity,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "deleteImage", required = false) List<Integer> deleteImageIds,
            RedirectAttributes redirectAttributes) {

        try {
            // 기존 공연 정보를 가져옴
            PerformancesEntity existingEntity = performancesRepository.findById(performancesEntity.getId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 공연 정보를 찾을 수 없습니다."));

            // 공연 정보 업데이트
            existingEntity.setTitle(performancesEntity.getTitle());
            existingEntity.setStartDate(performancesEntity.getStartDate());
            existingEntity.setEndDate(performancesEntity.getEndDate());
            existingEntity.setLocation(performancesEntity.getLocation());
            existingEntity.setTime(performancesEntity.getTime());
            existingEntity.setAge(performancesEntity.getAge());

            performancesRepository.save(existingEntity);

            // 삭제할 이미지가 있는 경우 처리
            if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
                List<ImagesEntity> images = imagesRepository.findAllById(deleteImageIds);
                for (ImagesEntity image : images) {
                    try {
                        fileService.deleteFile(image.getImageUrl());
                    } catch (Exception e) {
                        System.err.println("파일 삭제 중 예외 발생: " + image.getImageUrl());
                    }
                }
                imagesRepository.deleteAll(images);
            }

            // 새 파일이 업로드된 경우 처리
            if (file != null && !file.isEmpty()) {
                String filePath = fileService.saveFile(file);
                ImagesEntity image = new ImagesEntity();
                image.setImageUrl(filePath);
                image.setReferenceId(existingEntity.getId());
                image.setReferenceType(ImagesEntity.ReferenceType.PERFORMANCE);
                imagesRepository.save(image);
            }

            redirectAttributes.addFlashAttribute("message", "공연 정보가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "공연 정보 업데이트 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return "redirect:/admin/showCommitList";
    }

    /****************************************************************************** */

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
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {

        userService.updateUser(userId, loginId, name, email, phoneNumber, role, status);
        redirectAttributes.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
        return "redirect:/admin/userCommit"; // 수정 후 회원 목록 페이지로 이동
    }

    // 오늘의 예약 데이터를 가져와서 모델에 추가
    @GetMapping("/admin/todayReservations")
    public String getTodayReservations(Model model) {
        List<ReservationsEntity> reservations = reservationsService.getTodayPaidReservations();
        if (reservations == null || reservations.isEmpty()) {
            System.out.println("No reservations found for today.");
        } else {
            System.out.println("Reservations found: " + reservations.size());
        }
        model.addAttribute("reservations", reservations);
        return "admin/adminMenu";
    }
}
