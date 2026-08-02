package com.lms.courseservice.adapter.out.persistence;

import com.lms.courseservice.adapter.out.persistence.entity.CourseJpaEntity;
import com.lms.courseservice.adapter.out.persistence.mapper.CoursePersistenceMapper;
import com.lms.courseservice.adapter.out.persistence.mapper.LessonPersistenceMapper;
import com.lms.courseservice.adapter.out.persistence.mapper.ModulePersistenceMapper;
import com.lms.courseservice.application.port.out.CourseRepositoryPort;
import com.lms.courseservice.domain.enums.CourseStatus;
import com.lms.courseservice.domain.model.Course;
import com.lms.courseservice.domain.model.Lesson;
import com.lms.courseservice.domain.model.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CoursePersistenceAdapter implements CourseRepositoryPort {

    private final CourseJpaRepository courseJpaRepository;
    private final CoursePersistenceMapper courseMapper;
    private final ModuleJpaRepository moduleJpaRepository;
    private final ModulePersistenceMapper moduleMapper;
    private final LessonJpaRepository lessonJpaRepository;
    private final LessonPersistenceMapper lessonMapper;

    @Override
    public Course save(Course course) {
        CourseJpaEntity entity = courseMapper.toEntity(course);
        CourseJpaEntity saved = courseJpaRepository.save(entity);
        return courseMapper.toDomain(saved);
    }

    @Override
    public Optional<Course> findById(Long id) {
        return courseJpaRepository.findById(id).map(courseMapper::toDomain);
    }

    @Override
    public Optional<Course> findBySlug(String slug) {
        return courseJpaRepository.findBySlug(slug).map(courseMapper::toDomain);
    }

    @Override
    public List<Course> findAllByStatus(CourseStatus status) {
        return courseJpaRepository.findAllByStatus(status).stream()
                .map(courseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> findAll() {
        return courseJpaRepository.findAll().stream()
                .map(courseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        courseJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return courseJpaRepository.existsById(id);
    }

    @Override
    public Optional<Course> findByIdWithFullCurriculum(Long id) {
        return courseJpaRepository.findByIdWithFullCurriculum(id).map(courseMapper::toDomain);
    }
    @Override
    public Optional<Module> findModuleById(Long moduleId) {
        return moduleJpaRepository.findById(moduleId).map(entity -> {
            Module module = moduleMapper.toDomain(entity);
            // Gán tay back-reference vì đã ignore ở MapStruct
            module.setCourse(courseMapper.toDomain(entity.getCourse()));
            return module;
        });
    }
    @Override
    public Optional<Lesson> findLessonById(Long lessonId) {
        return lessonJpaRepository.findById(lessonId).map(entity -> {
            Lesson lesson = lessonMapper.toDomain(entity);
            // Gán back-reference: Module (chứa Course)
            lesson.setModule(moduleMapper.toDomain(entity.getModule()));
            lesson.getModule().setCourse(courseMapper.toDomain(entity.getModule().getCourse()));
            return lesson;
        });
    }
}
