// package: com.lms.courseservice.application.port.in
package com.lms.courseservice.application.port.in;

import com.lms.courseservice.domain.model.Course;
import java.util.List;

public interface GetCourseUseCase {
    List<Course> getAllPublishedCourses();
    Course getCourseDetail(String slug);
    Course getCurriculum(Long courseId);
}