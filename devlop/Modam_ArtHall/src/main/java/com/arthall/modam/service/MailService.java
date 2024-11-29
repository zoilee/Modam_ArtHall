package com.arthall.modam.service;

import java.util.Random;

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

    String header = "<h1>안녕하세요 ModamArtHall 입니다 </h1>";

    String footer = "<p>모담아트홀 | MODAM Art Hall</p><br><p>서울시 ○○구 ○○○로 ○○번길 123</p><br><p>전화: 02-1234-5678 | 이메일: info@modamarthall.com</p> ";

    // ==========================임시 비밀번호 발급=========================
    public static String generateTemporaryPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder tempPassword = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            tempPassword.append(characters.charAt(random.nextInt(characters.length())));
        }
    }

    public void mailSend(MailDto mailDto) throws Exception {
        return tempPassword.toString();
    }

    // 임시 비밀번호 전송 메서드
    public void sendTemporaryPassword(String email, String temporaryPassword) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setFrom(fromAddress);
            helper.setSubject("임시 비밀번호 안내");
            helper.setText(
                    "<h1>임시 비밀번호 안내</h1>" +
                            "<p>회원님의 임시 비밀번호는 다음과 같습니다.</p>" +
                            "<p><strong>" + temporaryPassword + "</strong></p>" +
                            "<p>로그인 후 반드시 비밀번호를 변경해주세요.</p>",
                    true);

            // 이메일 본문 동적 구성
            StringBuilder content = new StringBuilder();
            content.append(header)
                    .append(mailDto.getMessage())
                    .append(footer);

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
            throw new RuntimeException("메일 전송 중 오류가 발생했습니다.");
        }
    }

    public static boolean isTemporaryPassword(String password) {
        return password.length() == 10; // 10자리 임시 비밀번호 체크
    }

}
