package com.lms.courseservice.adapter.in.rest.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCurriculumResponse {
    private Long id;
    private String title;
    private String slug;
    private List<ModuleResponse> modules;
}
