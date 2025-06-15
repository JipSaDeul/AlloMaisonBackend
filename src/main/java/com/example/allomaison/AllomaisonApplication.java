package com.example.allomaison;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AllomaisonApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllomaisonApplication.class, args);
    }
}
