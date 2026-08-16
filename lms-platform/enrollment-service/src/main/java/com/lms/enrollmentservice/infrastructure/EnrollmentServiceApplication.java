package com.lms.enrollmentservice.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.lms.enrollmentservice",
        "com.lms.shared",
        "com.lms.security"
})
@EntityScan(basePackages = "com.lms.enrollmentservice.adapter.out.persistence.entity")
@EnableJpaRepositories(basePackages = "com.lms.enrollmentservice.adapter.out.persistence.repository")
@EnableJpaAuditing
public class EnrollmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnrollmentServiceApplication.class, args);
    }
}