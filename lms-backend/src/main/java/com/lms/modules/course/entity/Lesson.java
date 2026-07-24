package com.lms.modules.course.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.common.entity.AuditableEntity;
import com.lms.modules.course.enums.LessonType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "lessons")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
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
    @JsonIgnore
    @Builder.Default
    private List<LessonResource> resources = new ArrayList<>();
}
