package com.arthall.modam.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir; // 파일이 저장될 디렉토리 경로

    public String saveFile(MultipartFile file) throws IOException {
        // 실행 디렉토리를 devlop 폴더까지만 포함하도록 수정
        String basePath = System.getProperty("user.dir");
        String targetPath = "Modam_ArtHall" + File.separator + "Modam_ArtHall";
        System.out.println("현재 작업 디렉토리: " + basePath);
        System.out.println("기준 경로: " + targetPath);

        if (basePath.endsWith(targetPath)) {
            basePath = basePath.replace(targetPath, ""); // 중복 제거
        }

        // 절대 경로 생성
        Path absoluteDir = Paths.get(basePath, uploadDir).normalize();
        System.out.println("베이스 경로: " + basePath);
        System.out.println("업로드 경로: " + absoluteDir);

        // 디렉토리가 없으면 생성
        if (!Files.exists(absoluteDir)) {
            Files.createDirectories(absoluteDir);
        }

        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        // 파일이 저장되는 실제 경로
        Path filePath = absoluteDir.resolve(uniqueFileName);

        System.out.println("실제파일저장 경로" + filePath);
        // 업로드된 파일을 실제 폴더에 저장
        Files.write(filePath, file.getBytes());

        // 데이터베이스에 저장할 상대 경로
        // "/uploads/" + uniqueFileName; 경로 조정필요할듯?
        return "/" + uniqueFileName;
    }

    public void deleteFile(String filePath) throws IOException {
        // 실행 디렉토리 가져오기 + 업로드 경로 //File.separator 는 \/ 구분자 제공 ex)windows = \, unix = /
        String basePath = System.getProperty("user.dir") + File.separator + uploadDir;

        // 상대 경로를 절대 경로로 변환

        // uploadDir을 포함한 경로 생성
        Path baseDir = Paths.get(basePath, uploadDir).normalize();

        Path absolutePath = Paths.get(basePath, filePath).normalize();

        // 파일 삭제
        System.out.println("삭제할 파일 경로: " + absolutePath);

        Files.deleteIfExists(absolutePath);
    }

}
