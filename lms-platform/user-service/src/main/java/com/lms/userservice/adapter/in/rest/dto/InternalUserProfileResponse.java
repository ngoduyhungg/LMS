package com.lms.userservice.adapter.in.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternalUserProfileResponse {
    private String userId;
    private String email;
    private String fullName;
    private String avatarUrl;
}