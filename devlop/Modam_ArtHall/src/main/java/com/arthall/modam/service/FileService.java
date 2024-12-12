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

    // 업로드 경로를 절대 경로로 고정

    private final String uploadDir = "/works/img/upload";

    public String saveFile(MultipartFile file) throws IOException {

        // 절대경로
        Path absoluteDir = Paths.get(uploadDir.replace("\\", "/")).toAbsolutePath();
        System.out.println("업로드 경로: " + absoluteDir);

        // 디렉토리가 없으면 생성

        if (!Files.exists(absoluteDir)) {
            Files.createDirectories(absoluteDir);
        }

        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        // 파일이 저장되는 실제 경로
        Path filePath = absoluteDir.resolve(uniqueFileName);
        System.out.println("실제파일저장 경로 " + filePath.toString().replace("\\", "/") + "(정규화) :" + absoluteDir);

        // 업로드된 파일을 실제 폴더에 저장
        Files.write(filePath, file.getBytes());

        // "/uploads/" + uniqueFileName; 경로 조정 1202
        return "/uploads/" + uniqueFileName.replace("\\", "/");
    }

    public void deleteFile(String filePath) throws IOException {
        // 절대 경로 생성
        Path absolutePath = Paths.get(uploadDir).resolve(filePath).normalize();
        System.out.println("삭제할 파일 경로: " + absolutePath);

        // 파일 존재 여부 확인 후 삭제
        if (Files.exists(absolutePath)) {
            Files.delete(absolutePath);
            System.out.println("파일 삭제 성공: " + absolutePath);
        } else {
            System.err.println("파일이 존재하지 않음: " + absolutePath);
        }
    }

}