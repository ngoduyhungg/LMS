package com.lms.courseservice.domain.model;

import com.lms.courseservice.domain.enums.LessonType;
import com.lms.courseservice.domain.shared.AuditInfo;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {
    private Long id;
    private Module module;
    private String title;
    private String content;
    private String videoUrl;

    @Builder.Default
    private Integer durationSeconds = 0;

    @Builder.Default
    private LessonType lessonType = LessonType.VIDEO;

    @Builder.Default
    private Boolean isPreview = false;

    @Builder.Default
    private Integer sortOrder = 0;

    @Builder.Default
    private List<LessonResource> resources = new ArrayList<>();

    private AuditInfo auditInfo;

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
