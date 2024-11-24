package com.arthall.modam.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
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
                        .append("<p>고객님의 예약번호는 XX입니다.</p>");
    
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
    
         // ==========================임시 비밀번호 발급==========================
         public void sendTemporaryPassword(String to, String temporaryPassword) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // 이메일 기본 설정
            helper.setTo(to);
            helper.setSubject("임시 비밀번호 발급");
            helper.setText("<h1>임시 비밀번호 안내</h1>"
                    + "<p>임시 비밀번호: <strong>" + temporaryPassword + "</strong></p>"
                    + "<p>로그인 후 비밀번호를 반드시 변경해주세요.</p>", true);

            // 이메일 전송
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("임시 비밀번호 메일 전송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 추가된 메서드: 임시 비밀번호 생성
    public static String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

}
