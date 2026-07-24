package com.lms.modules.course.service;

import com.lms.modules.course.dto.*;

import java.util.List;

public interface CourseService {
    List<CourseResponse> getAllPublishedCourses();
    CourseResponse getCourseDetail(String slug);
    //Course CRUD & Curriculum
    CourseResponse createCourse(CourseUpsertRequest request, Long instructorId);
    CourseResponse updateCourse(Long id, CourseUpsertRequest request);
    void deleteCourse(Long id);
    CourseCurriculumResponse getCurriculum(Long courseId);

    //Module CRUD
    ModuleResponse addModule(Long courseId, ModuleUpsertRequest request);
    ModuleResponse updateModule(Long moduleId, ModuleUpsertRequest request);
    void deleteModule(Long moduleId);

    //Lesson CRUD
    LessonResponse addLesson(Long moduleId, LessonUpsertRequest request);
    LessonResponse updateLesson(Long lessonId, LessonUpsertRequest request);
    void deleteLesson(Long lessonId);
}