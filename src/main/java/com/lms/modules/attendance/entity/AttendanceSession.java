package com.lms.modules.attendance.entity;

import com.lms.common.entity.AuditableEntity;
import com.lms.modules.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Table(name = "attendance_sessions")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSession extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    @Column(name = "title", length = 255, nullable = false)
    private String title;
    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;
    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;
    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;
    @Column(name = "check_in_code", length = 20)
    private String checkInCode;
}
