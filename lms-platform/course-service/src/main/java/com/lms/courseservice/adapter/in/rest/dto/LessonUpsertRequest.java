package com.lms.courseservice.adapter.in.rest.dto;

import com.lms.courseservice.domain.enums.LessonType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

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
    private List<@Valid LessonResourceRequest> resources;

}
