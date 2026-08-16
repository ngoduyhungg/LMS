package com.lms.userservice.domain.model;

import com.lms.userservice.domain.enums.UserStatus;
import com.lms.userservice.domain.shared.AuditInfo;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private UserId id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String phoneNumber;
    private UserStatus status;
    private AuditInfo auditInfo;
}