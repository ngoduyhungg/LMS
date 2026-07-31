package com.lms.modules.quiz.entity;

import com.lms.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "question_options")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionOption extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question optionsQuestion;
    @Column(name = "content",nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "is_correct", nullable = false)
    @Builder.Default
    private Boolean isCorrect = false;
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
}
