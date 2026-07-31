package com.lms.courseservice.adapter.in.rest.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResourceResponse {
    private Long id;
    private String title;
    private String fileUrl;
    private String fileType;
    private Long fileSizeBytes;
}
