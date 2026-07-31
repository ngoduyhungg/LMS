package com.lms.courseservice.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.courseservice.domain.enums.LessonType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "lessons")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson extends com.lms.shared.entity.AuditableEntity {
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

    public static Lesson create(Module module, String title, String content, String videoUrl,
                                Integer durationSeconds, LessonType lessonType,
                                Boolean isPreview, Integer sortOrder){
        return Lesson.builder()
                .module(module)
                .title(title)
                .content(content)
                .videoUrl(videoUrl)
                .durationSeconds(durationSeconds != null ? durationSeconds : 0)
                .lessonType(lessonType != null ? lessonType : LessonType.VIDEO)
                .isPreview(isPreview != null ? isPreview : false)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .resources(new ArrayList<>())
                .build();
    }
    public void updateDetails(String title, String content, String videoUrl,
                              Integer durationSeconds, LessonType lessonType,
                              Boolean isPreview, Integer sortOrder){
        this.title = title;
        this.content = content;
        this.videoUrl = videoUrl;
        if(durationSeconds != null) this.durationSeconds = durationSeconds;
        if(lessonType != null) this.lessonType = lessonType;
        if(isPreview != null) this.isPreview = isPreview;
        if(sortOrder != null) this.sortOrder = sortOrder;
    }

    public void addResource(String title, String fileUrl, String fileType, Long fileSizeBytes){
        LessonResource resource = LessonResource.create(this, title, fileUrl, fileType, fileSizeBytes);
        this.resources.add(resource);
    }
    public void clearResources(){
        this.resources.clear();
    }
}
