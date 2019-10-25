package com.ksm.kakao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.ksm.kakao", exclude = SecurityAutoConfiguration.class)
public class TestMain {
    public static void main(String[] args) {
        SpringApplication.run(TestMain.class, args);
    }
}
