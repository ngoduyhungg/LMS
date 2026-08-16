package com.lms.userservice.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.lms.userservice",
        "com.lms.shared",
        "com.lms.security"
})
@EntityScan(basePackages = "com.lms.userservice.adapter.out.persistence.entity")
@EnableJpaRepositories(basePackages = "com.lms.userservice.adapter.out.persistence.repository")
@EnableJpaAuditing
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}