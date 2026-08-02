package com.lms.courseservice.application.port.in.command;

import com.lms.courseservice.domain.enums.CourseLevel;
import com.lms.courseservice.domain.enums.CourseStatus;

import java.math.BigDecimal;

public record CourseCommand(String title, String summary, String description, BigDecimal price, CourseLevel level, Long categoryId, String thumbnailUrl, CourseStatus status) {}
