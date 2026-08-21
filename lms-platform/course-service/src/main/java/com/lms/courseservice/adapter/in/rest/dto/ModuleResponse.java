package com.lms.courseservice.adapter.in.rest.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleResponse {
    private Long id;
    private String title;
    private Integer sortOrder;

    private List<LessonResponse> lessons;
}
