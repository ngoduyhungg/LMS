package com.lms.enrollmentservice.application.port.out.dto;

public record UserProfile(
        String userId,
        String email,
        String fullName,
        String avatarUrl
) {}