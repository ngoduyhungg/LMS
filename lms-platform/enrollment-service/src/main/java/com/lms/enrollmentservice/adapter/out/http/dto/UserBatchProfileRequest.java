package com.lms.enrollmentservice.adapter.out.http.dto;

import java.util.List;

public record UserBatchProfileRequest(List<String> userIds) {}