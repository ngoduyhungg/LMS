package com.lms.courseservice.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = "com.lms.courseservice"
)
@EntityScan(
        basePackages = "com.lms.courseservice.domain.model"
)
@EnableJpaRepositories(
        basePackages = "com.lms.courseservice.adapter.out.persistence"
)
public class CourseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                CourseServiceApplication.class,
                args
        );
    }
}