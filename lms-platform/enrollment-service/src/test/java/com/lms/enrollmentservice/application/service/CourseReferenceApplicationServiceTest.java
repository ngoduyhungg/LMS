package com.lms.enrollmentservice.application.service;

import com.lms.enrollmentservice.application.port.out.CourseReferenceRepositoryPort;
import com.lms.enrollmentservice.domain.model.CourseReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseReferenceApplicationServiceTest {

    @Mock
    private CourseReferenceRepositoryPort repositoryPort;

    @InjectMocks
    private CourseReferenceApplicationService courseMetricsApplicationService;

    @Test
    @DisplayName("1. Nên tạo mới CourseMetrics khi courseId chưa tồn tại trong hệ thống")
    void shouldCreateNewCourseMetricsWhenNotFound() {
        Long courseId = 1L;
        String instructorId = "5A1";
        int totalLessons = 5;

        when(repositoryPort.findByCourseId(courseId)).thenReturn(Optional.empty()); 
        when(repositoryPort.save(any(CourseReference.class))).thenAnswer(inv -> inv.getArgument(0));

        courseMetricsApplicationService.syncReference(courseId, instructorId ,totalLessons);

        ArgumentCaptor<CourseReference> captor = ArgumentCaptor.forClass(CourseReference.class);
        verify(repositoryPort, times(1)).save(captor.capture()); 

        CourseReference saved = captor.getValue();
        assertThat(saved.getCourseId()).isEqualTo(courseId);
        assertThat(saved.getTotalLessons()).isEqualTo(totalLessons);
    }

    @Test
    @DisplayName("2. Nên cập nhật tổng số bài học khi CourseMetrics đã tồn tại")
    void shouldUpdateExistingCourseMetricsWhenFound() {
        Long courseId = 1L;
        String instructorId = "5A1";
        int initialLessons = 3;
        int updatedLessons = 8;

        CourseReference existing = CourseReference.builder()
                .courseId(courseId)
                .totalLessons(initialLessons)
                .build();

        when(repositoryPort.findByCourseId(courseId)).thenReturn(Optional.of(existing)); 
        when(repositoryPort.save(any(CourseReference.class))).thenAnswer(inv -> inv.getArgument(0));

        courseMetricsApplicationService.syncReference(courseId, instructorId, updatedLessons);

        verify(repositoryPort, times(1)).save(any(CourseReference.class));
        assertThat(existing.getTotalLessons()).isEqualTo(updatedLessons);
    }

    @Test
    @DisplayName("3. Nên đồng bộ 0 bài học thành công cho khóa học mới tạo")
    void shouldSyncZeroLessonsForNewCourse() {
        Long courseId = 2L;
        String instructorId = "5A1";
        int totalLessons = 0;

        when(repositoryPort.findByCourseId(courseId)).thenReturn(Optional.empty()); 
        when(repositoryPort.save(any(CourseReference.class))).thenAnswer(inv -> inv.getArgument(0));

        courseMetricsApplicationService.syncReference(courseId, instructorId, totalLessons);

        verify(repositoryPort, times(1)).save(any(CourseReference.class));
    }

    @Test
    @DisplayName("4. Nên cập nhật số lượng bài học về 0 cho khóa học đã tồn tại")
    void shouldUpdateExistingMetricsToZeroLessons() {
        Long courseId = 1L;
        String instructorId = "5A1";
        CourseReference existing = CourseReference.builder()
                .courseId(courseId)
                .totalLessons(10)
                .build();

        when(repositoryPort.findByCourseId(courseId)).thenReturn(Optional.of(existing)); 
        when(repositoryPort.save(any(CourseReference.class))).thenAnswer(inv -> inv.getArgument(0));

        courseMetricsApplicationService.syncReference(courseId, instructorId,0);

        assertThat(existing.getTotalLessons()).isEqualTo(0);
        verify(repositoryPort, times(1)).save(any(CourseReference.class));
    }

    @Test
    @DisplayName("5. Nên xử lý đồng bộ số lượng bài học lớn (ví dụ 500 bài học)")
    void shouldSyncLargeNumberOfLessonsSuccessfully() {
        Long courseId = 99L;
        String instructorId = "5A1";
        int largeLessons = 500;

        when(repositoryPort.findByCourseId(courseId)).thenReturn(Optional.empty()); 
        when(repositoryPort.save(any(CourseReference.class))).thenAnswer(inv -> inv.getArgument(0));

        courseMetricsApplicationService.syncReference(courseId, instructorId, largeLessons);

        verify(repositoryPort, times(1)).save(any(CourseReference.class));
    }

    @Test
    @DisplayName("6. Phải luôn gọi findByCourseId để kiểm tra trạng thái trước khi save")
    void shouldAlwaysVerifyFindByCourseIdCalledBeforeSave() {
        Long courseId = 1L;
        String instructorId = "5A1";
        when(repositoryPort.findByCourseId(courseId)).thenReturn(Optional.empty()); 
        when(repositoryPort.save(any(CourseReference.class))).thenAnswer(inv -> inv.getArgument(0));

        courseMetricsApplicationService.syncReference(courseId, instructorId, 5);

        verify(repositoryPort, times(1)).findByCourseId(courseId); 
        verify(repositoryPort, times(1)).save(any(CourseReference.class));
    }

    @Test
    @DisplayName("7. Nên giữ nguyên đúng courseId khi thực hiện cập nhật metric")
    void shouldRetainCorrectCourseIdOnUpdate() {
        Long courseId = 77L;
        String instructorId = "5A1";
        CourseReference existing = CourseReference.builder()
                .courseId(courseId)
                .totalLessons(2)
                .build();

        when(repositoryPort.findByCourseId(courseId)).thenReturn(Optional.of(existing)); 
        when(repositoryPort.save(any(CourseReference.class))).thenAnswer(inv -> inv.getArgument(0));

        courseMetricsApplicationService.syncReference(courseId, instructorId, 15);

        assertThat(existing.getCourseId()).isEqualTo(courseId);
    }

    @Test
    @DisplayName("8. Nên xử lý ổn thỏa khi gọi đồng bộ liên tiếp nhiều lần trên cùng một courseId")
    void shouldHandleMultipleSequentialSyncsSuccessfully() {
        Long courseId = 1L;
        String instructorId = "5A1";
        CourseReference metrics = CourseReference.builder()
                .courseId(courseId)
                .totalLessons(1)
                .build();

        when(repositoryPort.findByCourseId(courseId)).thenReturn(Optional.of(metrics)); 
        when(repositoryPort.save(any(CourseReference.class))).thenAnswer(inv -> inv.getArgument(0));

        courseMetricsApplicationService.syncReference(courseId, instructorId, 3);
        courseMetricsApplicationService.syncReference(courseId, instructorId, 5);

        assertThat(metrics.getTotalLessons()).isEqualTo(5);
        verify(repositoryPort, times(2)).save(any(CourseReference.class));
    }

    @Test
    @DisplayName("9. Nên đồng bộ thành công cho khóa học chỉ có đúng 1 bài học")
    void shouldSyncSingleLessonCourseSuccessfully() {
        Long courseId = 3L;
        String instructorId = "5A1";

        when(repositoryPort.findByCourseId(courseId)).thenReturn(Optional.empty()); 
        when(repositoryPort.save(any(CourseReference.class))).thenAnswer(inv -> inv.getArgument(0));

        courseMetricsApplicationService.syncReference(courseId, instructorId, 1);

        verify(repositoryPort, times(1)).save(any(CourseReference.class));
    }

    @Test
    @DisplayName("10. Nên thực hiện đúng quy trình khởi tạo qua cơ chế orElseGet của Optional")
    void shouldInvokeOrElseGetCreationPathWhenMetricsNotFound() {
        Long courseId = 4L;
        String instructorId = "5A1";
        when(repositoryPort.findByCourseId(courseId)).thenReturn(Optional.empty()); 
        when(repositoryPort.save(any(CourseReference.class))).thenAnswer(inv -> inv.getArgument(0));

        courseMetricsApplicationService.syncReference(courseId, instructorId, 4);

        verify(repositoryPort, times(1)).findByCourseId(courseId); 
        verify(repositoryPort, times(1)).save(any(CourseReference.class));
    }
}