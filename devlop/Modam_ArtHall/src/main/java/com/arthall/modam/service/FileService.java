package com.arthall.modam.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir; // 파일이 저장될 디렉토리 경로

    public String saveFile(MultipartFile file) throws IOException {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        // 파일이 저장되는 실제 경로
        String filePath = uploadDir + uniqueFileName;
        System.out.println(filePath);
        // 업로드된 파일을 실제 폴더에 저장
        Files.write(Paths.get(filePath), file.getBytes());

        // 데이터베이스에 저장할 상대 경로
        // "/uploads/" + uniqueFileName; 경로 조정필요할듯?
        return "/" + uniqueFileName;
    }

    public void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }

}
