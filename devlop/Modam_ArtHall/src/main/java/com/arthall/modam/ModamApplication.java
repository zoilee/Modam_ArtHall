package com.arthall.modam;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.arthall.modam.entity"})
public class ModamApplication {

    public static void main(String[] args) {
 

        SpringApplication.run(ModamApplication.class, args);
    }
}
