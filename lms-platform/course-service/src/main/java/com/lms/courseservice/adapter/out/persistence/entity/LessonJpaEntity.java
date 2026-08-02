package com.lms.courseservice.adapter.out.persistence.entity;

import com.lms.courseservice.domain.enums.LessonType;
import com.lms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonJpaEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleJpaEntity module;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "duration_seconds", nullable = false)
    @Builder.Default
    private Integer durationSeconds = 0;

    @Column(name = "lesson_type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LessonType lessonType = LessonType.VIDEO;

    @Column(name = "is_preview", nullable = false)
    @Builder.Default
    private Boolean isPreview = false;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LessonResourceJpaEntity> resources = new ArrayList<>();
}
