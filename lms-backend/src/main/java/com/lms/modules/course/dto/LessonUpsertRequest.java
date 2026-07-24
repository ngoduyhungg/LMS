package com.lms.modules.course.dto;

import com.lms.modules.course.enums.LessonType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonUpsertRequest {
    @NotBlank(message = "Title cannot be blank!")
    private String title;
    private String content;
    private String videoUrl;
    @Min(0)
    private Integer durationSeconds;
    @NotNull(message = "Lesson type cannot be null!")
    private LessonType lessonType;
    private Boolean isPreview;
    @Min(0)
    private Integer sortOrder;

}
