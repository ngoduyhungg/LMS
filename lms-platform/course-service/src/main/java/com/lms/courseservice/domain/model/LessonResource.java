package com.lms.courseservice.domain.model;

import jakarta.persistence.*;
import lombok.*;


@Table(name = "lesson_resources")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResource extends com.lms.shared.entity.AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
    @Column(name = "title", length = 255, nullable = false)
    private String title;
    @Column(name = "file_url", length = 500, nullable = false)
    private String fileUrl;
    @Column(name = "file_type", length = 50)
    private String fileType;
    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

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
