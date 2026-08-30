package com.lms.enrollmentservice.adapter.in.rest.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        int page,
        int size,
        long total
) {}