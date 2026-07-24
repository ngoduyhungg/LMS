package com.lms.modules.quiz.entity;

import com.lms.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "submission_answers")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionAnswer extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private QuizSubmission submission;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption option;
    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;
    @Column(name = "score_earned", precision = 5, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal scoreEarned = new BigDecimal("0.00");
}
