package com.fc.mini3server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Mini3ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Mini3ServerApplication.class, args);
    }

}
