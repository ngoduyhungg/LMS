package com.lms.enrollmentservice.adapter.out.persistence.entity;

import com.lms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateJpaEntity extends AuditableEntity {

    @Column(name = "course_id", nullable = false, unique = true)
    private Long courseId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "template_url", nullable = false, length = 500)
    private String templateUrl;
}
