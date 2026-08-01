package com.lms.courseservice.adapter.in.rest.dto;

import lombok.*;

import java.util.List;

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
    private List<LessonResourceRequest> resources;
}
