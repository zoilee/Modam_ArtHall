package com.arthall.modam.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class MailDto {
    private String address;
    private String title;
    private String message;
    private MultipartFile file;
}
