package com.lms.courseservice.application.port.in;

import com.lms.courseservice.application.port.in.command.CourseCommand;
import com.lms.courseservice.domain.model.Course;

public interface ManageCourseUseCase {
    Course createCourse(CourseCommand request);
    Course updateCourse(Long id, CourseCommand request);
    void deleteCourse(Long id);
}