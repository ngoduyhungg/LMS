package com.lms.courseservice.adapter.in.rest.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {
    private Long id;
    private String title;
    private String slug;

    private String summary;
    private String level;
    private Long categoryId;

    private String description;
    private String thumbnailUrl;
    private BigDecimal price;
    private String status;

    // Thông tin của Giảng viên (Lấy từ bảng Users)
    private String instructorId;
    private String instructorName;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
