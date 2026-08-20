package com.lms.userservice.adapter.in.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private String userId;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String phoneNumber;
    private String status;
}