package com.arthall.modam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.arthall.modam.dto.MailDto;
import com.arthall.modam.service.MailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class MailController {
    @Autowired
    private MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<String> execMail(MailDto mailDto) {
        try {
            mailService.mailSend(mailDto);
            return ResponseEntity.ok("메일 전송 성공!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("메일 전송 실패: " + e.getMessage());
        }
    }

}
