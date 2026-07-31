package com.lms.modules.certificate.entity;

import com.lms.common.entity.AuditableEntity;
import com.lms.modules.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "certificates")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate extends AuditableEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, unique = true)
    private Course course;
    @Column(name = "title", length = 255, nullable = false)
    private String title;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "template_url", length = 500, nullable = false)
    private String templateUrl;
}
