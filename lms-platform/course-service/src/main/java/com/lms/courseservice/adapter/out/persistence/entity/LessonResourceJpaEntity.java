package com.lms.courseservice.adapter.out.persistence.entity;

import com.lms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesson_resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResourceJpaEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private LessonJpaEntity lesson;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "file_url", length = 500, nullable = false)
    private String fileUrl;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;
}
