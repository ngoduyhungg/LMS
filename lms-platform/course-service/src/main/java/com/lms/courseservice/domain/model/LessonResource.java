package com.lms.courseservice.domain.model;

import com.lms.courseservice.domain.shared.AuditInfo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResource {
    private Long id;
    private Lesson lesson;
    private String title;
    private String fileUrl;
    private String fileType;
    private Long fileSizeBytes;

    private AuditInfo auditInfo;

    public static LessonResource create(Lesson lesson, String title, String fileUrl, String fileType, Long fileSizeBytes){
        return LessonResource.builder()
                .lesson(lesson)
                .title(title)
                .fileUrl(fileUrl)
                .fileType(fileType)
                .fileSizeBytes(fileSizeBytes)
                .build();
    }
}
