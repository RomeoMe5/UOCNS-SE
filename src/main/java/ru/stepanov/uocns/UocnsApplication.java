package ru.stepanov.uocns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = "ru.stepanov.uocns")
public class UocnsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UocnsApplication.class, args);
    }

}
