package com.arthall.modam.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
