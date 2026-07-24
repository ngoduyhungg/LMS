package com.lms.common.enums;

public enum ErrorCode {

    // ===== COMMON =====

    INTERNAL_SERVER_ERROR,

    VALIDATION_ERROR,

    ILLEGAL_ARGUMENT,

    RESOURCE_NOT_FOUND,

    ACCESS_DENIED,

    UNAUTHORIZED,


    // ===== USER =====

    USER_NOT_FOUND,

    USER_ALREADY_EXISTS,


    // ===== COURSE =====

    COURSE_NOT_FOUND,

    COURSE_ALREADY_EXISTS,


    // ===== CATEGORY =====

    CATEGORY_NOT_FOUND,


    // ===== AUTH =====

    INVALID_TOKEN,

    TOKEN_EXPIRED
}
