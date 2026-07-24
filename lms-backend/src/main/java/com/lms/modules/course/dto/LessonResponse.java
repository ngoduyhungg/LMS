package com.lms.modules.course.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponse {
    private Long id;
    private String title;
    private String lessonType;
    private Integer durationSeconds;
    private Boolean isPreview;
    private Integer sortOrder;
}
