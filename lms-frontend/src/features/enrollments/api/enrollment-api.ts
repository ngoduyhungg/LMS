import { axiosClient } from '@/shared/lib/axios';
import type { EnrollmentFilterParams } from '../types/enrollment-filter-params-type';
import type { EnrollmentResponse, CourseEnrollmentSummary, StudentEnrollmentDetail } from '../types/enrollment-type';

const API_URL_PREFIX = '/api/enrollments';

export const enrollmentRoleAdminApi = {
  getAll: async (params: EnrollmentFilterParams) => {
    const res = await axiosClient.get(API_URL_PREFIX, { params });
    return res.data;
  },
  getDetail: async (id: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/${id}`);
    return res.data;
  },
  create: async (payload: any) => {
    const res = await axiosClient.post(API_URL_PREFIX, payload);
    return res.data;
  },
  update: async (id: string, payload: any) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}`, payload);
    return res.data;
  },
  active: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/active`);
    return res.data;
  },
  remove: async (id: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/${id}`);
    return res.data;
  },
};

export const enrollmentRoleStudentApi = {
  pause: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/pause`);
    return res.data;
  },
  remove: async (id: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/${id}`);
    return res.data;
  },
};

// ==========================================
// UI-4 STUDENT LEARNING API CONTRACT
// ==========================================
export const studentLearningApi = {
  enroll: async (courseId: string | number): Promise<EnrollmentResponse> => {
    const res = await axiosClient.post('/api/enrollments', { courseId: Number(courseId) });
    return res.data;
  },

  getMyEnrollments: async (): Promise<EnrollmentResponse[]> => {
    const res = await axiosClient.get('/api/enrollments');
    return res.data;
  },

  getEnrollmentDetail: async (courseId: string | number): Promise<EnrollmentResponse> => {
    const res = await axiosClient.get(`/api/enrollments/courses/${courseId}`);
    return res.data;
  },

  updateProgress: async (
    courseId: string | number, 
    payload: { lessonId: string | number; watchedSeconds: number; isCompleted: boolean }
  ): Promise<EnrollmentResponse> => {
    const res = await axiosClient.put(`/api/enrollments/courses/${courseId}/progress`, payload);
    return res.data;
  }
};

// ==========================================
// UI-5 ADMIN ENROLLMENT MANAGEMENT CONTRACT
// ==========================================
export const adminEnrollmentApi = {
  // LEVEL 1: Lấy danh sách thống kê Ghi danh theo từng Khóa học
  getCourseSummaries: async (): Promise<CourseEnrollmentSummary[]> => {
    const res = await axiosClient.get('/api/admin/enrollments/courses/summary');
    return res.data;
  },

  // LEVEL 2: Lấy danh sách Học viên của 1 Khóa học (Có hỗ trợ phân trang)
  getCourseEnrollments: async (courseId: string | number, params?: { page?: number; size?: number }): Promise<any> => {
    const res = await axiosClient.get(`/api/admin/enrollments/courses/${courseId}`, { params });
    return res.data;
  },

  // FORCE CANCEL
  cancel: async (id: string | number): Promise<StudentEnrollmentDetail> => {
    const res = await axiosClient.put(`/api/admin/enrollments/${id}/cancel`);
    return res.data;
  }
};