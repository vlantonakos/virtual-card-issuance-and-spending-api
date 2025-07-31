package com.cardplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CardPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardPlatformApplication.class, args);
    }
}