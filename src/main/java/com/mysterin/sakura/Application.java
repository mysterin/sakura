package com.mysterin.sakura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.class.path"));
        SpringApplication.run(Application.class, args);
    }
}
