package com.lms.modules.quiz.entity;

import com.lms.common.entity.AuditableEntity;
import com.lms.modules.quiz.enums.QuestionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "questions")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "question_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private QuestionType questionType = QuestionType.SINGLE_CHOICE;
    @Column(name = "points", precision = 5, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal points = new BigDecimal("1.00");
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
}
