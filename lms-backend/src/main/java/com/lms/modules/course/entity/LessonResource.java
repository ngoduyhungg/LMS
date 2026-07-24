package com.lms.modules.course.entity;

import com.lms.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;


@Table(name = "lesson_resources")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResource extends AuditableEntity {
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
}
