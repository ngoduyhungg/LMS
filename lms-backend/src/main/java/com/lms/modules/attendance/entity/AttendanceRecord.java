package com.lms.modules.attendance.entity;

import com.lms.common.entity.AuditableEntity;
import com.lms.modules.attendance.enums.AttendanceRecordsStatus;
import com.lms.modules.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Table(name = "attendance_records", uniqueConstraints = {@UniqueConstraint(name = "uk_attendance_records_session_user", columnNames = {"session_id", "user_id"})})
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecord extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private AttendanceSession session;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AttendanceRecordsStatus status = AttendanceRecordsStatus.PRESENT;
    @Column(name = "check_in_time")
    private OffsetDateTime checkInTime;
    @Column(name = "note", length = 255)
    private String note;
}
