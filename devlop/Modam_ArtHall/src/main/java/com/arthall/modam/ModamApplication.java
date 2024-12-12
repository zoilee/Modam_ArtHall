package com.arthall.modam;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.arthall.modam.entity"})
public class ModamApplication {

    public static void main(String[] args) {
        // 명시적으로 .env 파일 로드
        Dotenv dotenv = Dotenv.configure()
                .directory("C:/springboot/Modam_ArtHall") // .env 파일 경로 설정
                .load();

        // 환경 변수 등록
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(ModamApplication.class, args);
    }
    
}
