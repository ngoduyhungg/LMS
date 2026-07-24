package com.lms.modules.course.service.impl;

import com.lms.common.exception.custom.ResourceNotFoundException;
import com.lms.modules.auth.entity.User;
import com.lms.modules.auth.repository.UserRepository;
import com.lms.modules.course.dto.*;
import com.lms.modules.course.entity.*;
import com.lms.modules.course.entity.Module;
import com.lms.modules.course.enums.CourseStatus;
import com.lms.modules.course.mapper.CourseMapper;
import com.lms.modules.course.mapper.LessonMapper;
import com.lms.modules.course.mapper.ModuleMapper;
import com.lms.modules.course.repository.*;
import com.lms.modules.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    //repo
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    //mapstruct mappers
    private final CourseMapper courseMapper;
    private final ModuleMapper moduleMapper;
    private final LessonMapper lessonMapper;

    private String generateSlug(String title) {
        if (title == null) return "";
        return title.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    @Override
    @Transactional(readOnly=true)
    public List<CourseResponse> getAllPublishedCourses(){
        return courseMapper.toResponseList(courseRepository.findAllByStatus(CourseStatus.PUBLISHED));
    }
    @Override
    @Transactional(readOnly=true)
    public CourseResponse getCourseDetail(String slug){
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with this slug: " + slug));
        return courseMapper.toResponse(course);
    }
    //Course CRUD & Curriculum
    @Override
    @Transactional
    public CourseResponse createCourse(CourseUpsertRequest request, Long instructorId){
        User instructor = userRepository.findById(instructorId).orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        Category category = null;
        if(request.getCategoryId() != null){
            category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        }
        String slug = generateSlug(request.getTitle()) + "-" + System.currentTimeMillis();
        Course course = courseMapper.toEntity(request, category, instructor, slug);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toResponse(savedCourse);
    }
    @Override
    @Transactional
    public CourseResponse updateCourse(Long id, CourseUpsertRequest request){
        Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        courseMapper.updateEntityFromRequest(request, course);

        if(request.getCategoryId() != null){
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id:" + request.getCategoryId()));
            course.setCategory(category);
        }

        Course updatedCourse = courseRepository.save(course);
        return courseMapper.toResponse(updatedCourse);
    }
    @Override
    @Transactional
    public void deleteCourse(Long id){
        Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        courseRepository.delete(course);
    }
    @Override
    @Transactional(readOnly = true)
    public CourseCurriculumResponse getCurriculum(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        return courseMapper.toCurriculumResponse(course);
    }
    //Module CRUD
    @Override
    @Transactional
    public ModuleResponse addModule(Long courseId, ModuleUpsertRequest request){
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        Module module = moduleMapper.toEntity(request, course);
        return moduleMapper.toResponse(moduleRepository.save(module));
    }
    @Override
    @Transactional
    public ModuleResponse updateModule(Long moduleId, ModuleUpsertRequest request){
        Module module = moduleRepository.findById(moduleId).orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        moduleMapper.updateEntityFromRequest(request, module);
        Module updateModule = moduleRepository.save(module);

        return moduleMapper.toResponse(updateModule);
    }
    @Override
    @Transactional
    public void deleteModule(Long moduleId){
        Module module = moduleRepository.findById(moduleId).orElseThrow(() -> new ResourceNotFoundException("Module not found with id:" + moduleId));
        moduleRepository.delete(module);
    }
    //Lesson CRUD
    @Override
    @Transactional
    public LessonResponse addLesson(Long moduleId, LessonUpsertRequest request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        Lesson lesson = lessonMapper.toEntity(request, module);

        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }
    @Override
    @Transactional
    public LessonResponse updateLesson(Long lessonId, LessonUpsertRequest request){
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id:" + lessonId));
        lessonMapper.updateEntityFromRequest(request, lesson);
        Lesson updatedLesson = lessonRepository.save(lesson);

        return lessonMapper.toResponse(updatedLesson);
    }
    @Override
    @Transactional
    public void deleteLesson(Long lessonId){
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id:" + lessonId));
        lessonRepository.delete(lesson);
    }

}
