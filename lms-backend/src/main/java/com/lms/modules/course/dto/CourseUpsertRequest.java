package com.lms.modules.course.dto;

import com.lms.modules.course.enums.CourseLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseUpsertRequest {
    @NotBlank(message = "Title cannot be blank!")
    @Size(max = 255)
    private String title;
    @Size(max = 500)
    private String summary;
    private String description;
    @DecimalMin("0.00")
    private BigDecimal price;
    @NotNull(message = "Course level cannot be null!")
    private CourseLevel level;
    private Long categoryId;
    private String thumbnailUrl;
}
