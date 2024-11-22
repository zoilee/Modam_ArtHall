package com.arthall.modam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.arthall.modam.dto.MailDto;

import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from-address}")
    private String fromAddress;

    public void mailSend(MailDto mailDto) throws Exception {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // 이메일 기본 설정
            helper.setTo(mailDto.getAddress());
            helper.setFrom(fromAddress);
            helper.setSubject(mailDto.getTitle());

            // 이메일 본문 동적 구성
            StringBuilder content = new StringBuilder();
            content.append("<h1>안녕하세요</h1>")
                    .append(mailDto.getMessage());

            // 첨부파일 추가
            MultipartFile file = mailDto.getFile();
            if (file != null && !file.isEmpty()) {
                String safeFilename = checkFilename(file.getOriginalFilename());
                helper.addAttachment(safeFilename, new ByteArrayResource(file.getBytes()));

                // 파일이 있을 때만 본문에 안내 추가
                content.append("<p>첨부된 파일을 확인하세요.</p>");
            }

            // 이메일 본문 설정
            helper.setText(content.toString(), true);

            // 이메일 전송
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("메일 전송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 이름 검증
    private String checkFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

}
