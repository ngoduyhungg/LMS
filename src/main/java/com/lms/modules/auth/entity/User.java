package com.lms.modules.auth.entity;

import com.lms.common.entity.AuditableEntity;
import com.lms.modules.auth.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Table(name = "users")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AuditableEntity {
    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;
    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;
    @Column(name = "full_name", length = 150, nullable = false)
    private String fullName;
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private UserStatus status = UserStatus.ACTIVE;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}
