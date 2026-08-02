package com.lms.courseservice.application.port.in.command;

public record CategoryCommand(String name, String description, Long parentCategoryId) {}
