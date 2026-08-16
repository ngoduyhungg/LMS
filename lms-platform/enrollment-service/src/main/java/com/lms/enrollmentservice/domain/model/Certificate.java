package com.lms.enrollmentservice.domain.model;

import com.lms.enrollmentservice.domain.shared.AuditInfo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {
    private Long id;
    private Long courseId;
    private String title;
    private String templateUrl;

    private AuditInfo auditInfo;

    public void updateTemplate(String title, String templateUrl) {
        this.title = title;
        this.templateUrl = templateUrl;
    }
}
