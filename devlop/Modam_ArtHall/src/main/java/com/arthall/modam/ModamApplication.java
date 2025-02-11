package com.arthall.modam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EntityScan(basePackages = { "com.arthall.modam.entity" })
public class ModamApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory("/var/lib/jenkins/workspace/yayaya/devlop/Modam_ArtHall")
                .load();

        // 프로퍼티 설정 .
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(),
                entry.getValue()));

        SpringApplication.run(ModamApplication.class, args);
    }

}
