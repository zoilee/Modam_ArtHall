package com.arthall.modam.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.arthall.modam.entity.TemporaryImagesEntity;
import com.arthall.modam.repository.ImagesRepository;
import com.arthall.modam.repository.TemporaryImagesRepository;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Value("${custom.upload-dir}") // 내가 지정한 경로
    private String uploadDir;

    @Value("${custom.temp-dir}") // 임시 파일 경로
    private String tempDir;

    private final TemporaryImagesRepository temporaryImagesRepository;

    private final ImagesRepository imagesRepository;

    public FileUploadController(TemporaryImagesRepository temporaryImagesRepository,
            ImagesRepository imagesRepository) {
        this.temporaryImagesRepository = temporaryImagesRepository;
        this.imagesRepository = imagesRepository;

    }

    @PostMapping("/upload-temp")
    public ResponseEntity<String> uploadTempImage(@RequestParam("file") MultipartFile file) {
        try {
            // 파일 이름 생성
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            Path filePath = Paths.get(tempDir, uniqueFileName);

            Files.createDirectories(filePath.getParent());

            // 파일 저장
            Files.write(filePath, file.getBytes());

            // 데이터베이스에 파일 경로와 토큰 저장
            String fileUrl = "/uploads/temp/" + uniqueFileName;
            String referenceToken = UUID.randomUUID().toString();
            System.out.println("Generated referenceToken: " + referenceToken);

            TemporaryImagesEntity tempImage = new TemporaryImagesEntity();
            tempImage.setImageUrl(fileUrl);
            tempImage.setReferenceToken(referenceToken);
            temporaryImagesRepository.save(tempImage);

            return ResponseEntity.ok(referenceToken); // referenceToken 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("임시파일저장실패");
        }

    }

}
